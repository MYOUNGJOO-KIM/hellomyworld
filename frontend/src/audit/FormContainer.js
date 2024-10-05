import React, {useEffect, useState, useRef, useReducer, useCallback } from 'react'; 
import styled from 'styled-components';
import './../assets/css/app.css';
import './../assets/css/form_container.css';
import Input from '../board/FormInput';
import BoardList from '../board/BoardList';
import BoardButton from './../board/BoardButton';
import axios from 'axios';
import ReactJsPagination from "react-js-pagination";
import icon_search from './../assets/images/icon_search.png';
import Select from './../board/FormSelect'
import { useDropzone } from 'react-dropzone';
import Header from './Header';
import CategoryListTree from './CategoryListTree';
import { format } from "date-fns";
import CategoryListModal from './CategoryListModal';
import { CategoryContext, useCategoryContext } from '../CategoryContexts';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import { ko } from 'date-fns/locale';


function FormContainer( props ) {
    const apiBaseUrl = process.env.REACT_APP_API_BASE_URL;
    
    //모달관련
    const [isModalOpen, setIsModalOpen] = useState(false);

    const { cleanParam } = useCategoryContext();

    const treeRef = useRef();
    const [selectedKeys, setSelectedKeys] = useState([]);
    const [selectedInfo, setSelectedInfo] = useState(null);

    const [categoryListTreeList, setCategoryListTreeList] = useState([]);

    const [catCd, setCatCd] = useState('');
    const [catSeq, setCatSeq] = useState('');
    const [inputCatNm, setInputCatNm] = useState('');

    const [prfSeq, setPrfSeq] = useState('');
    const [inputPrfNm, setInputPrfNm] = useState('');
    const [inputPrfDesc, setInputPrfDesc] = useState('');
    const [inputOcrTextOutput, setInputOcrTextOutput] = useState('');
    const [inputOcrTextPlaceHolder, setInputOcrTextPlaceHolder] = useState('OCR TEXT OUTPUT');

    const [fileList, setFileList] = useState([]);
    const [mgtList, setMgtList] = useState([]);

    const [selectSearchKey, setSelectSearchKey] = useState('');//select
    const [inputSearchStr, setInputSearchStr] = useState('');//text

    const [prfData, setPrfData] = useState('');

    const [isMgtVisible, setIsMgtVisible] = useState(false);
    
    const [dynamicInputVal, setDynamicInputVal] = useState({});
    const [dynamicInputValStr, setDynamicInputValStr] = useState({});
    
    const [formDisabled, setFormDisabled] = useState(true);
    const [treeMgtYn, setTreeMgtYn] = useState('');

    const [isTreeOpen, setIsTreeOpen] = useState(true);

    const getCategoryListTreeList = async ( ) => {
        let response;
        try {
          response = await axios.post(`${apiBaseUrl}/category/getTree`, {mgtYn:treeMgtYn});//, {'content-type' : "application/json"}
          setCategoryListTreeList(response.data);
        } catch (error) {
            response = error;
        } finally {
            return response;
        }
    }

    useEffect(() => {
        mgtList.map((mgt, i)=>{
            const inputKey = 'input_'+i;
            const inputKeyStr = inputKey+'_str';
            setDynamicInputVal(prevObject => initDynamicInputVal(prevObject, inputKey));
            setDynamicInputValStr(prevObject => initDynamicInputVal(prevObject, inputKeyStr));
        });
    },[mgtList]);

    useEffect(() => {
        if(isTreeOpen){
            getCategoryListTreeList();
        }
    },[]);

    if (!isTreeOpen) return null;

    const requestHeader = {
        searchKey : selectSearchKey
        , searchStr : inputSearchStr
        , fileList : fileList
    };
    
    const requestBody = {
        catSeq : catSeq
        , catNm : inputCatNm//get tree
        , catCd : catCd//get tree
        //, prfSeq : prfSeq 
        //무조건 insert로 바뀌어서 잠시 해제
        , prfNm : inputPrfNm
        , prfDesc : inputPrfDesc
        //, fileList : fileList
        //, mgtList : mgtList
    };
    
    const selectOptions = [
        {key:'', value:'검색옵션 선택'}
        , {key : 'catNm', value : '카테고리 이름'}
        , {key : 'catCd', value : '카테고리 코드'}
    ];

    const getPrfData = async () => {
        const rqParams = cleanParam(requestHeader);
        const rqBody = cleanParam(requestBody);
        //어디서 requestBody에 fileList랑 mgtList를 넣는지 모르겠음.
        //컨트롤러 PrfData 객체에 저 필드가 없어서 바인딩에 오류남.
        delete rqBody.fileList;
        delete rqBody.mgtList;
        
        let response;
        try {
      
            response = (await axios.post(`${apiBaseUrl}/data/getData`, rqBody, {params:rqParams}));

            if(response.data != null){
                setIsMgtVisible(false);
                if(response.data != '' && response.data.mgtList && response.data.mgtList != ''){
                    setIsMgtVisible(true);
                    setMgtList(response.data.mgtList);
                }
            }
          } catch (error) {
            response = error;
          } finally {
            return response;
        }
    }

    const getFileUploadDto = async (file) => {

        //어디서 requestBody에 fileList랑 mgtList를 넣는지 모르겠음.
        //컨트롤러 PrfData 객체에 저 필드가 없어서 바인딩에 오류남.
        setInputOcrTextPlaceHolder();
        const formData = new FormData();
        formData.append('files', {});
        // 파일을 FormData에 추가
        for (let i = 0; i < requestHeader.fileList.length; i++) {
            formData.append('files', requestHeader.fileList[i]);
        }
        
        if(formData.get('files')){
            let response = await axios.post(`${apiBaseUrl}/data/getFileUploadDto`, formData, {
                headers: {
                  'Content-Type': 'multipart/form-data',
                },
            });
    
            setInputOcrTextOutput(response.data.ocrTextOutput);
        }
        
    }

    const savePrfData = async ( ) => {
        if(validation(requestBody)){
            const rqBody = cleanParam(requestBody);
            const formData = new FormData();
            const emptyBlob = new Blob();

            if(fileList.length == 0){

                formData.append('files', emptyBlob, 'empty_file.jpg');
            }

            // 파일을 FormData에 추가
            for (let i = 0; i < fileList.length; i++) {
                formData.append('files', fileList[i]);
            }
            
            // 추가적인 데이터도 FormData에 추가
            formData.append('filePath', '/'+rqBody.catSeq+'/');
            formData.append('requestBody', JSON.stringify(rqBody));
            
            let arr = [];
            
            for (let i = 0; i < Object.keys(dynamicInputValStr).length; i++){

                //let obj = {}
                //obj.prfSeq = prfSeq; 아직 insert뿐이라 prfSeq가 없음.
                //obj.content = dynamicInputValStr[Object.keys(dynamicInputValStr)[i]]; list형태로 안들어가서 이상하지만 일단 주석
                arr.push(dynamicInputValStr[Object.keys(dynamicInputValStr)[i]]);
                //newMgtList[i].content = dynamicInputValStr[Object.keys(dynamicInputValStr)[i]];
            }
            formData.append('dataMgtList', arr);

  
            let response;
            try {
                if(prfSeq > 0){
                    response = (await axios.put(`${apiBaseUrl}/data/put`, rqBody));
                } else {
                    response = await axios.post(`${apiBaseUrl}/data/post`, formData, {
                        headers: {
                          'Content-Type': 'multipart/form-data',
                        },
                    });
                }
                
                alert("카테고리 업데이트 완료");
            } catch (error) {
                alert("카테고리 업데이트 실패");
                response = error;
            } finally {
                return response;
            }
        }
    };
    
    const handleCloseModal = () => {
        searchReset();
        setIsModalOpen(false);
    };

    const setInput = function (prfData, prfAttach){
        setPrfSeq(prfData.prfSeq);
        setInputPrfNm(prfData.prfNm);
        setInputPrfDesc(prfData.prfDesc);
        setFileList(prfAttach.fileList);
        setMgtList(prfAttach.mgtList);
    
        requestBody.prfSeq = prfData.prfSeq;
        requestBody.prfNm = prfData.prfNm;
        requestBody.prfDesc = prfData.prfDesc;
        // requestBody.fileList = prfAttach.fileList;
        // requestBody.mgtList = prfAttach.mgtList;
    }

    const stateClear = function(){
        setPrfData('');
        setMgtList([]);
        setIsMgtVisible(false);
        setDynamicInputVal({});
        setDynamicInputValStr({});
    }

    const inputClear = function(){//우선 전체 초기화라고 가정. 취소 버튼 시 동작만 있음.
        //getCategoryListTreeList({});
        
        setPrfSeq('');
        setInputPrfNm('');
        setInputPrfDesc('');
        setInputOcrTextOutput('');
        setInputOcrTextPlaceHolder('OCR TEXT OUTPUT');
        setFileList([]);
        setMgtList([]);
    
        requestBody.prfSeq = '';
        requestBody.prfNm = '';
        requestBody.prfDesc = '';
        requestBody.fileList = [];
        requestBody.mgtList = [];

        //setIsMgtVisible(false);
        
        //setFormDisabled(true);
        //setMgtList([]);
        //setSelectedKeys([]);
    }

    const validation = function (data) {

        if(data.catNm == ''){
          alert('카테고리명이 필요합니다');
          return false;
        }
        if(data.catCd == ''){
          alert('카테고리 코드가 필요합니다');
          return false;
        }
        if(data.prfNm == ''){
          alert('제목이 필요합니다');
          return false;
        }
        if(data.prfDesc == ''){
          alert('내용이 필요합니다');
          return false;
        }


        //관리항목 공란 체크

        //const inputValues = {};

        const hasEmptyFields = Object.values(dynamicInputValStr).some(value => value == '');
        if (hasEmptyFields) {
            alert('관리 항목을 입력해주세요.');
            return;
        }

        return true;
    }

    const searchReset = function(e){
        //catCd는 필수로 변경되었다는 전제하에
        requestHeader.searchKey = '';
        requestHeader.searchStr = '';
        setSelectSearchKey(''); 
        setInputSearchStr(''); 
    }
    
    const searchTextOnKeyUp = function(e){
        if(e.key == 'Enter'){
            //getChildCategoryList()
        }
    }

    const onTreeSelect = (selectedKeys, info, d,e,f) => {
        inputClear();
        stateClear();
        if(selectedKeys.length == 0){
            setSelectedKeys([]);
            setSelectedInfo(null);
            setCatCd('');
            setCatSeq('');
            setInputCatNm('');
            requestBody.catSeq = '';
            requestBody.catCd = '';
            requestBody.prfSeq = '';
            //inputClear();
            setFormDisabled(true);
        } else {
            setSelectedKeys(selectedKeys);
            setSelectedInfo(info);
            setInputCatNm(info.node.title);
            setCatCd(selectedKeys[0]);
            setCatSeq(info.node.catSeq);
            requestBody.catSeq = info.node.catSeq;
            requestBody.catCd = selectedKeys[0];
            requestBody.catNm = info.node.catNm;
            
            getPrfData();
            setFormDisabled(false);
        }
    };

    function onRowClick(selectedObj) {

        
        setSelectedKeys([selectedObj.catCd]);
        setCatCd(selectedObj.catCd);
        setCatSeq(selectedObj.catSeq);
        setInputCatNm(selectedObj.catNm);
        
        requestBody.catSeq = selectedObj.catSeq;
        requestBody.catCd = selectedObj.catCd;
        requestBody.catNm = selectedObj.catNm;
        
        inputClear();
        getPrfData();
        
        setFormDisabled(false);
          
        return handleCloseModal();
    }

    const datepickerOnchange = function (inputKey, date) {
        let inputData = '';
        let DateStr = '';
        if (date instanceof Date) {
            inputData = date;
            DateStr = new Date(date.getTime()-(date.getTimezoneOffset() * 60000)).toISOString();
        } else {
            inputData = date.target.value;
            DateStr = inputData;
        }
        console.log(DateStr);

        setDynamicInputVal(prevObject => ({ ...prevObject, [inputKey] : inputData}));
        setDynamicInputValStr(prevObject => ({ ...prevObject, [inputKey+'_str'] : DateStr}));
        
    }

    const initDynamicInputVal = function (prevObject, inputKey) {
        return {
            ...prevObject,
            [inputKey]: ''
        };
    };
    

    const mkElements = function (mgt, i) {
        const inputKey = 'input_'+i;
        switch(mgt.dataType){
            case 'yyyyMMddhh' :  return <div key={i} className='mgt_box datepicker'><div><span className="ol_dot"></span><h3>{mgt.mgtNm}</h3></div><DatePicker label="Basic date picker" className="icon"  selected={dynamicInputVal[inputKey]} onChange={(date) => datepickerOnchange(inputKey, date)} dateFormat="yyyy-MM-dd HH" locale={ko} showTimeInput placeholderText='연월일과 시간을 선택하세요.'/></div>;
            // selected={dynamicInputVal[inputKey]}  onChange={(date) => datepickerOnchange(inputKey, date)} ref={(el) => dynamicInputRefs.current[inputKey] = el}
            case 'yyyy' : return <div key={i} className='mgt_box datepicker'><div><span className="ol_dot"></span><h3>{mgt.mgtNm}</h3></div><DatePicker className="icon" selected={dynamicInputVal[inputKey]} onChange={(date) => datepickerOnchange(inputKey, date)} dateFormat="yyyy" locale={ko} showYearPicker placeholderText='연도를 선택하세요.'/></div>;
            case 'MM' : return <div key={i} className='mgt_box datepicker'><div><span className="ol_dot"></span><h3>{mgt.mgtNm}</h3></div><input type='number' defaultValue=""  selected={dynamicInputVal[inputKey]} onChange={(date) => datepickerOnchange(inputKey, date)} placeholder='월을 입력하세요.' min="1" max="12" step="1"/> 월</div>;
            case 'dd' : return <div key={i} className='mgt_box datepicker'><div><span className="ol_dot"></span><h3>{mgt.mgtNm}</h3></div><input type='number'  selected={dynamicInputVal[inputKey]} onChange={(date) => datepickerOnchange(inputKey, date)} placeholder='일을 입력하세요.' min="1" max="31" step="1"/> 일</div>;
            // case 'hh' : return <div key={i} className='mgt_box datepicker'><div><span className="ol_dot"></span><h3>{mgt.mgtNm}</h3></div><input type='number' placeholder='시간을 입력하세요.' min="1" max="24" step="1"/> 시</div>;
            // case 'date' : return <div key={i} className='mgt_box datepicker'><div><span className="ol_dot"></span><h3>{mgt.mgtNm}</h3></div><DatePicker className="icon" selected={startDate} onChange={(date) => setStartDate(date)} dateFormat="yyyy-MM-dd" locale={ko} placeholderText='연월일을 선택하세요.'/></div>;
            // case 'datetime' : return <div key={i} className='mgt_box datepicker'><div><span className="ol_dot"></span><h3>{mgt.mgtNm}</h3></div><DatePicker className="icon" selected={startDate} onChange={(date) => setStartDate(date)} dateFormat="yyyy-MM-dd HH:mm:ss" locale={ko} showTimeInput placeholderText='연월일과 시간을 선택하세요.'/></div>;
            // case 'dayoftheweek' : return <div key={i} className='mgt_box datepicker'><div><span className="ol_dot"></span><h3>{mgt.mgtNm}</h3></div><input type='text' placeholder='요일을 입력하세요.'/> 요일</div>;
            case 'text' : return <div key={i} className='mgt_box'><div><span className="ol_dot"></span><h3>{mgt.mgtNm}</h3></div><input type='text' selected={dynamicInputVal[inputKey]} onChange={(date) => datepickerOnchange(inputKey, date)} placeholder='텍스트를 입력하세요.'/></div>;
            case 'number' : return <div key={i} className='mgt_box'><div><span className="ol_dot"></span><h3>{mgt.mgtNm}</h3></div><input type='number' selected={dynamicInputVal[inputKey]} onChange={(date) => datepickerOnchange(inputKey, date)} placeholder='숫자를 입력하세요.'/></div>;
            default : return <div key={i} className='mgt_box'><div><span className="ol_dot"></span><h3>{mgt.mgtNm}</h3></div><input type='text'  selected={dynamicInputVal[inputKey]} onChange={(date) => datepickerOnchange(inputKey, date)} placeholder='텍스트를 입력하세요.'/></div>;
        }
    }

    return ( 
        <div className='root_box admin'>
            <div className='form'>
            <Header title='증빙자료 관리'/>
            <div className='contents_box'>
                <div className='content_box category_box'>
                    <div className='content_header'>트리뷰</div> 
                    <CategoryListTree treeRef={treeRef} onClick={onTreeSelect} selectedKeys={selectedKeys} mgtYn={treeMgtYn} treeData={categoryListTreeList}/>
                </div>
                <div className='dnm_content'>
                    <div className='content_box form_box'>
                        <div className='content_header'>
                            <div className='search_box'>
                                <Select options = {selectOptions} placeholder_str='검색옵션 선택 후 검색어를 입력하세요.' onChange={(event)=>{setSelectSearchKey(event.target.value);}} value={selectSearchKey}/>
                                <div className="input_container text">
                                    <input type="text" placeholder='검색옵션 선택 후 검색어를 입력하세요.' onChange={(event)=>{setInputSearchStr(event.target.value);}} onKeyUp={(event)=>{searchTextOnKeyUp(event)}} value={inputSearchStr}></input>
                                    <div className='button_box n2 reset'>
                                        <button className='get' onClick={() => {setIsModalOpen(true);}}><img src={(icon_search)}/>검색</button>
                                        <BoardButton type="reset" onClick={searchReset}/>
                                    </div>
                                </div>

                            </div>
                        </div>
                        
                        <div className='input_container_box'>
                            <Input classNm='title' type = 'text' value={inputCatNm} onChange={(event)=>{setInputCatNm(event.target.value);}} label_str = '카테고리명' placeholder_str='카테고리명을 선택하세요.' disabled={true}/>
                        </div>

                        <div className='form_table'>
                            <Input type = 'hidden' value={prfSeq} onChange={(event)=>{setPrfSeq(event.target.value);} } />
                            <div className='row head'>
                                <div className='label_row'>제목</div>
                                <div className='content_row'>
                                    <input type="text" value={inputPrfNm} placeholder='제목을 입력하세요.' disabled={formDisabled} onChange={(event)=>{setInputPrfNm(event.target.value);}}/>
                                </div>
                            </div>
                            <div className='row body'>
                                <div className='label_row'>내용</div>
                                <div className='content_row'>
                                    <textarea type="text" value={inputPrfDesc} placeholder='제목을 입력하세요.' disabled={formDisabled} onChange={(event)=>{setInputPrfDesc(event.target.value);}}></textarea>
                                </div>
                            </div>
                            <div className='row foot'>
                                <div className='label_row'>첨부파일</div>
                                <div className='content_row'>
                                    <MyDropzone fileList={fileList} setFileList={setFileList} rqHeader={requestHeader} onChange={getFileUploadDto}/>
                                </div>
                            </div>
                            <div className='row ocrview'>
                                <div className='label_row'>OCR INPUT RESULT</div>
                                <div className='content_row'>
                                    <textarea type="text" value={inputOcrTextOutput} disabled={true} placeholder={inputOcrTextPlaceHolder}></textarea>
                                </div>
                            </div>
                            {isMgtVisible && (
                                <div className='row etc' >
                                    <div className='label_row'>관리항목</div>
                                    <div className='content_row'>
                                        {mgtList.map((mgt, i)=>(
                                            mkElements(mgt, i)
                                        ))}
                                    </div>
                                </div>
                            )}
                        </div>
                        <div className="foot_btn_box">
                            <BoardButton type="save" color="orange" disabled={formDisabled} onClick={savePrfData}/>
                        </div>
                    </div>
                </div>
            </div>
            <CategoryListModal isOpen={isModalOpen} onClose={handleCloseModal} searchKey={selectSearchKey} searchStr={inputSearchStr} onRowClick={onRowClick} />
            </div>
        </div>
        
    );

}

export default FormContainer;

const MyDropzone = (props) => {

    const {getRootProps, getInputProps, acceptedFiles} = useDropzone({
        accept: {
            "image/*": [".jpeg", ".jpg", ".png"],
            "application/pdf": [".pdf"]
        },
        onDrop: (acceptedFiles) => {
            // 파일 리스트를 상태로 업데이트
            props.setFileList(acceptedFiles);
            props.rqHeader.fileList = acceptedFiles;
            // props.setFileList(prevFiles => [
            //     ...prevFiles,
            //     ...acceptedFiles
            // ]);
            props.onChange(acceptedFiles);
        },
    });

    const files = props.fileList.map(file => (
        // <div key={file.name}></div>
      <li key={file.name}>
        {file.name} - {file.size} bytes
      </li>
    ));
    return (
        <section>
            <div {...getRootProps({ className: 'dropzone' })}>
                <input {...getInputProps()} />
                <p>클릭 또는 드래그&드랍으로 업로드 할 수 있습니다(이미지 파일 or PDF 파일)</p>
                <aside>
                    <h4></h4>
                    <ul>{files}</ul>
                </aside>
            </div>
        </section>
    );
}