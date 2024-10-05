import React, {useEffect, useState, useRef} from 'react';
import './../assets/css/dt.css';
import './../assets/css/dt_attach.css';
import BoardList from '../board/BoardList';
import BoardButton from './../board/BoardButton';
import ReactJsPagination from "react-js-pagination";
import SearchBox from './../board/SearchBox';
import CategoryListTree from './CategoryListTree';
import BoardDateTerm from './../board/BoardDateTerm';
import axios from 'axios';
import { CategoryContext, useCategoryContext } from '../CategoryContexts';

import 'react-datepicker/dist/react-datepicker.css';

function DtAttachContainer(props){
    const clientBaseUrl = process.env.REACT_APP_CLIENT_BASE_URL;
    const apiBaseUrl = process.env.REACT_APP_API_BASE_URL;

    const { cleanParam } = useCategoryContext();
    const [categoryListTreeList, setCategoryListTreeList] = useState([]);
    const [childCategoryAttachList, setChildCategoryAttachList] = useState([]);
    const [selectedKeys, setSelectedKeys] = useState([]);
    const [selectedInfo, setSelectedInfo] = useState(null);
    const [rowClickAttachSeq, setRowClickAttachSeq] = useState(null);

    const [inputSearchStd, setInputSearchStd] = useState();
    const [inputSearchEd, setInputSearchEd] = useState();
    const [selectSearchKey, setSelectSearchKey] = useState('');//select
    const [inputSearchStr, setInputSearchStr] = useState('');//text

    const [formDisabled, setFormDisabled] = useState(true);
    
    const [reactJsPgListSize, setReactJsPgListSize] = useState(0);
    const reactJsPgRef = useRef(null);

    const [treeMgtYn, setTreeMgtYn] = useState('');
    const [treePrintYn, setTreePrintYn] = useState('');
    
    const [isTreeOpen] = useState(true);
    const [reactJsPgShow, setReactJsPgShow] = useState(false);

    const getCategoryListTreeList = async ( ) => {
        let response;
        try {
            response = await axios.post(`${apiBaseUrl}/category/getTree`, {
                mgtYn:treeMgtYn, printYn:treePrintYn
            });
            setCategoryListTreeList(response.data);
        } catch (error) {
            response = error;
        } finally {
            return response;
        }
    }

    useEffect(() => {
        if(isTreeOpen){
            getCategoryListTreeList();
            getChildCategoryAttachList();
        }
    },[]);
    
    if (!isTreeOpen) return null;
    
    const requestHeader = {
        page: 1
        , size: 14
        , searchKey : selectSearchKey
        , searchStr : inputSearchStr
        , searchStd : inputSearchStd
        , searchEd : inputSearchEd
    };
    
    const requestBody = {
        catCd : selectedKeys.length > 0 ? selectedKeys[0] : null
    };

    const selectOptions = [
        {key : 'catNm', value : '카테고리 이름'}
        , {key : 'catCd', value : '카테고리 코드'}
        , {key : 'printNm', value : '제목'}
        , {key : 'printDesc', value : '설명'}
    ];

    const getChildCategoryAttachList = async (newRequestHeader) => {
        if(newRequestHeader){
            requestHeader.searchKey = newRequestHeader.searchKey ? newRequestHeader.searchKey : requestHeader.searchKey;
            requestHeader.searchStr = newRequestHeader.searchStr ? newRequestHeader.searchStr : requestHeader.searchStr;
            requestHeader.page = newRequestHeader.page ? newRequestHeader.page : requestHeader.page;
            requestHeader.size = newRequestHeader.size ? newRequestHeader.size : requestHeader.size;
            requestHeader.searchStd = newRequestHeader.searchStd ? newRequestHeader.searchStd : requestHeader.searchStd;
            requestHeader.searchEd = newRequestHeader.searchEd ? newRequestHeader.searchEd : requestHeader.searchEd;
        }

        if(requestHeader.searchStd){
            requestHeader.searchStd = new Date(requestHeader.searchStd.getTime() - (requestHeader.searchStd.getTimezoneOffset() * 60000)).toISOString().slice(0, -1);
        }
        if(requestHeader.searchEd){
            requestHeader.searchEd.setHours(23, 59, 59, 0);
            requestHeader.searchEd = new Date(requestHeader.searchEd.getTime() - (requestHeader.searchEd.getTimezoneOffset() * 60000)).toISOString().slice(0, -1);
        }

        const rqHeader = cleanParam(requestHeader);
        const rqBody = cleanParam(requestBody);
        
        let response;

        try {
            response = await axios.post(`${apiBaseUrl}/attachment/getChildCategoryAttachList`, rqBody, {params : rqHeader});
            
            let totalCnt = 0;

            setFormDisabled(true);
            if(response.data != null && response.data != ''){
                totalCnt = response.data[0].totalCnt;
                
                setReactJsPgShow(true);
                setFormDisabled(false);
            }
            setReactJsPgListSize(totalCnt);

            if(reactJsPgRef.current){
                reactJsPgRef.current.updateListSize(totalCnt);
            }

            setChildCategoryAttachList(response.data);
            
        } catch (error) {
            response = error;
        } finally {
            return response;
        }
    };

    const stateClear = function(){
        setSelectedKeys([]);
        setSelectedInfo(null);
        setRowClickAttachSeq(null);
        
        setChildCategoryAttachList([]);
        requestHeader.catCd = null;
        setFormDisabled(false);

        setReactJsPgListSize(0);
        setReactJsPgShow(false);
        
          
        if(reactJsPgRef.current){
            reactJsPgRef.current.updateListSize(0);
            reactJsPgRef.current.updateActivePage();
        }
    }

    const inputClear = function(){
        setInputSearchStd('');
        setInputSearchEd('');
        setSelectSearchKey('');
        setInputSearchStr('');
        requestHeader.searchKey = '';
        requestHeader.searchStr = '';
        requestHeader.searchStd = '';
        requestHeader.searchEd = '';
    }

    const search = function(e){
        setInputSearchStd(inputSearchStd);
        setInputSearchEd(inputSearchEd);
        setSelectSearchKey(selectSearchKey); 
        setInputSearchStr(inputSearchStr); 
        requestHeader.searchStd = inputSearchStd;
        requestHeader.searchEd = inputSearchEd;
        requestHeader.searchKey = selectSearchKey;
        requestHeader.searchStr = inputSearchStr;

        getChildCategoryAttachList();
    }

    const searchReset = function(e){
        setInputSearchStd('');
        setInputSearchEd('');
        setSelectSearchKey(''); 
        setInputSearchStr(''); 
        requestHeader.searchStd = '';
        requestHeader.searchEd = '';
        requestHeader.searchKey = '';
        requestHeader.searchStr = '';

        //페이지네이션 초기화
        if(reactJsPgRef.current){
            reactJsPgRef.current.updateActivePage();
        }

        getChildCategoryAttachList();
    }

    const onTreeSelect = (selectedKeys, info, d,e,f) => {
        stateClear();
        inputClear();

        if(selectedKeys.length > 0){

            setSelectedKeys(selectedKeys);
            setSelectedInfo(info);
            requestBody.catCd = selectedKeys[0];
        } else {
            requestBody.catCd = '';
        }

        getChildCategoryAttachList();
    }

    const onRowClick = function(selectedObj){
        if(rowClickAttachSeq){
            setRowClickAttachSeq(null);
            inputClear();
        } else {
            setRowClickAttachSeq(selectedObj.attachSeq);
        }
    }

    const inputSearchStrOnKeyUp = function(e){
        if(e.key == 'Enter'){
            search();
        }
    }

    const openPopUp = function(){
        const updatedHeader = {
            searchKey : requestHeader.searchKey
            , searchStr : requestHeader.searchStr
            , searchStd : requestHeader.searchStd
            , searchEd : requestHeader.searchEd
            , page: 0
            , size: reactJsPgListSize
        };
        if(updatedHeader.searchStd){
            updatedHeader.searchStd = new Date(updatedHeader.searchStd.getTime() - (updatedHeader.searchStd.getTimezoneOffset() * 60000)).toISOString().slice(0, -1);
        }
        if(updatedHeader.searchEd){
            requestHeader.searchEd.setHours(23, 59, 59, 0);
            updatedHeader.searchEd = new Date(updatedHeader.searchEd.getTime() - (updatedHeader.searchEd.getTimezoneOffset() * 60000)).toISOString().slice(0, -1);
        }

        updatedHeader.printYn = 'y';
        updatedHeader.catCd = requestBody.catCd;

        window.open(
            `${clientBaseUrl}/dt/sapa/attach/preview?requestHeader=`+JSON.stringify(updatedHeader),
            'popupWindow',         // 팝업의 이름 (선택 사항)
            'width=1150,height=1000,scrollbars=yes,resizable=yes' // 팝업의 옵션
            //2480px(가로) X 3508px(세로) A4
            // 1920px, 2716px
        );
    }

    return (
        <div className='dt root_box'>
            <div className='dt_attach'>
            <div className=' contents_box'>
                <div className='content_box category_box'>
                    <div className='content_header'>트리뷰</div>
                    <CategoryListTree onClick={onTreeSelect} selectedKeys={selectedKeys} mgtYn={treeMgtYn} treeData={categoryListTreeList}/>
                </div>
                <div className='dnm_content'>

                    <div className='content_box'>
                        <div className='header_prt'>
                            <div className='header_box'>
                                <div className='label'>조회 시기</div>
                                <div className='content'>
                                    <BoardDateTerm searchStdValue={inputSearchStd} searchEdValue={inputSearchEd} searchStdOnChange={(e)=>{setInputSearchStd(e);}} searchEdOnChange={(e)=>{setInputSearchEd(e);}}/>
                                    
                                </div>
                            </div>
                            <div className='header_box'>
                                <div className='label'>조회 조건</div>
                                <SearchBox options={selectOptions} searchSelectValue={selectSearchKey} searchTextValue={inputSearchStr} searchTextOnChange={(e)=>{setInputSearchStr(e.target.value);}} searchTextOnKeyUp={(e)=>{inputSearchStrOnKeyUp(e);}} searchSelectOnChange={(e)=>{setSelectSearchKey(e.target.value);}} search={search} reset={searchReset} placeholder_str='검색어를 입력하세요.'/>
                            </div>
                        </div>
                        <div className='pagenation_parent'>
                            <div className='pagenation_box'>
                                <div className='label'>총 {reactJsPgListSize}개</div>
                                <div className={reactJsPgShow ? 'pg show' : 'pg hide'}>
                                    <ReactJsPg ref={reactJsPgRef} getList={getChildCategoryAttachList} requestHeader={requestHeader} requestBody={requestBody}/>
                                </div>
                            </div>
                            <BoardButton type='dtAttachPrintAll' onClick={() => {openPopUp();}} show={!formDisabled}/>
                        </div>
                        <BoardList type="DtAttach" placeholder_str='검색옵션 선택 후 검색어를 입력하세요.'  thead={[{key : 'catCd', value : '카테고리 코드'},{key : 'catNm', value : '카테고리 이름'}, {key : 'prfNm', value : '제목'}, {key : 'prfDesc', value : '설명'}, {key : 'fileNm', value : '파일명'}, {key : 'inDt', value : '등록 일자'}]} tbody={childCategoryAttachList} selectedId={rowClickAttachSeq}/>
                    </div>
                </div>
            </div>
            </div>

        </div>
    );
}

export default DtAttachContainer;

class ReactJsPg extends React.Component {
    constructor(props) {
      super(props);

      this.state = {
        activePage: 1,
        listSize : 0,
        pageSize : 14
      };
    }

    updateActivePage = () => {
        this.setState({activePage : 1});
      }

    updateListSize = (newSize) => {
        this.setState({listSize : newSize});
    }
   
    handlePageChange(pageNumber) {
      console.log(`Active page is ${pageNumber}`);

      this.setState({ activePage: pageNumber }, () => {
        if (this.props.getList) {
          const updatedHeader = {
            searchKey : this.props.requestHeader.searchKey
            , searchStr : this.props.requestHeader.searchStr
            , page: this.state.activePage
            , size: this.state.pageSize
            , searchStd : this.props.requestHeader.searchStd
            , searchEd : this.props.requestHeader.searchEd
          };
          this.props.getList(updatedHeader);
        }
      });

    }
   
    render() {
      return (
        <ReactJsPagination
            activePage={this.state.activePage}//Required
            totalItemsCount={this.state.listSize}//Required
            onChange={this.handlePageChange.bind(this)}//Required
            itemsCountPerPage={this.state.pageSize}
            pageRangeDisplayed={5}
            innerClass='pagination'//ul className
            itemClass='n5'//li className
            activeClass='active'//li active className
            activeLinkClass=''//a active className
            itemClassFirst='last_item'//<< className
            itemClassPrev='prev_item'//< className
            itemClassNext='next_item'//> className
            itemClassLast='last_item'//>> className
            //test중. 리스트 페이지 2개 이상일 시엔 보이는지?
            hideDisabled={false}
        />
      );
    }
}





