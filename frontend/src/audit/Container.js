import React, {useEffect, useRef, useState} from 'react';  
import './../assets/css/app.css';
import './../assets/css/container.css';
import Input from '../board/FormInput';
import BoardList from '../board/BoardList';
import Select from './../board/FormSelect'
import BoardButton from './../board/BoardButton';
import ReactJsPagination from "react-js-pagination";
import SearchBox from "./../board/SearchBox";
import Header from './Header';
import CategoryListTree from './CategoryListTree';
import axios from 'axios';
import { CategoryContext, useCategoryContext } from '../CategoryContexts';

function Container( props ){ 
  const apiBaseUrl = process.env.REACT_APP_API_BASE_URL;

  const { cleanParam } = useCategoryContext();
  const [categoryListTreeList, setCategoryListTreeList] = useState([]);

  const [ChildCategoryList, setChildCategoryList] = useState([]);
  const [upCatSelectOptions, setUpCatSelectOptions] = useState([{key : '', value : '선택 없음'}]);
  
  const [selectedKeys, setSelectedKeys] = useState([]);
  const [selectedInfo, setSelectedInfo] = useState(null);
  const [rowClickCatCd, setRowClickCatCd] = useState('');
  const [rowClickCatSeq, setRowClickCatSeq] = useState('');

  const [catSeq, setCatSeq] = useState('');
  const [inputCatNm, setInputCatNm] = useState('');
  const [inputCatCd, setInputCatCd] = useState('');
  const [inputUpCatCd, setInputUpCatCd] = useState('');
  const [inputUpCatNm, setInputUpCatNm] = useState('');
  const [inputPrintYn, setInputPrintYn] = useState('y');//false
  const [inputMgtYn, setInputMgtYn] = useState('y');//false

  const [selectSearchKey, setSelectSearchKey] = useState('');//select
  const [inputSearchStr, setInputSearchStr] = useState('');//text
  
  const [showDelBtn, setShowDelBtn] = useState(false);
  const [showEditBtn, setShowEditBtn] = useState(false);
  const [showEditCancelBtn, setShowEditCancelBtn] = useState(false);

  const [reactJsPgListSize, setReactJsPgListSize] = useState(0);
  const reactJsPgRef = useRef(null);

  const [treeMgtYn, setTreeMgtYn] = useState('');
  const [treePrintYn, setTreePrintYn] = useState('');

  const [isTreeOpen] = useState(true);
  const [reactJsPgShow, setReactJsPgShow] = useState(false);

  const [formStateStr, setFormStateStr] = useState('i');


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
      inputClear();
      getCategoryListTreeList();
      getChildCategoryList();
      getParentCategoryList();
    }
  },[]);

  if (!isTreeOpen) return null;

  const formState = {state : formStateStr, treeSelected : {selectedKeys : selectedKeys, selectedInfo : selectedInfo}, rowSelected : {rowClickCatSeq : rowClickCatSeq, rowClickCatCd : rowClickCatCd}};
  
  const requestHeader = {
    page: 1
    , size: 10
    , searchKey : selectSearchKey
    , searchStr : inputSearchStr
  };

  const requestBody = {
    catSeq : catSeq
    , catNm : inputCatNm
    , catCd : inputCatCd
    , upCatCd : inputUpCatCd
    , upCatNm : inputUpCatNm
    , printYn : inputPrintYn
    , mgtYn : inputMgtYn
  };

  const selectOptions = [
    {key : 'catNm', value : '카테고리 이름'}
    , {key : 'catCd', value : '카테고리 코드'}
  ];

  const getChildCategoryList = async (newRequestHeader, newRequestBody) => {


    if (formState.rowSelected.rowClickCatSeq > 0) {//로우 클릭 중
      switch(formState.state){
        case 'i' : 
          requestBody.catSeq = formState.treeSelected.selectedInfo.node.catSeq;
          requestBody.catCd = formState.treeSelected.selectedKeys[0];
        break;
        case 'u' : 
          // 카테고리 수정상태일 시 search 하려면 지금은 모두 insert로 바꾸고 실행하게 해놓음
          
        break;
      }

    } else if (formState.treeSelected.selectedKeys.length > 0) {//트리 클릭 중
      if( formState.treeSelected.selectedInfo.node ){//아니 node없다고 오류남
        switch(formState.state){
          case 'i' : 
            requestBody.catSeq = formState.treeSelected.selectedInfo.node.catSeq;
            requestBody.catCd = formState.treeSelected.selectedKeys[0];
            
            //여기부턴 insert시 하위리스트 조회일 때를 가정
            requestBody.upCatCd = '';
            requestBody.upCatNm = '';
            requestBody.mgtYn = '';
            requestBody.printYn = '';
          break;
          case 'u' :
            requestBody.catSeq = formState.treeSelected.selectedInfo.node.catSeq;
            requestBody.catCd = formState.treeSelected.selectedKeys[0];
            
          break;
        }
        
      } else {

        requestBody.catSeq = selectedInfo.node.catSeq;
        requestBody.catCd = selectedKeys[0];
      }
    } else {//로우 클릭X, 트리 클릭X
      requestBody.catSeq = '';
      requestBody.catCd = '';
      requestBody.upCatCd = '';
      requestBody.upCatNm = '';
      requestBody.mgtYn = '';
      requestBody.printYn = '';
    }

    if(newRequestHeader){
      //requestHeader.id = newRequestHeader.id ? newRequestHeader.id : requestHeader.id;
      //requestBody.catSeq = newRequestBody.catSeq ? newRequestBody.catSeq : requestBody.catSeq;
      requestHeader.searchKey = newRequestHeader.searchKey ? newRequestHeader.searchKey : requestHeader.searchKey;
      requestHeader.searchStr = newRequestHeader.searchStr ? newRequestHeader.searchStr : requestHeader.searchStr;
      requestHeader.page = newRequestHeader.page ? newRequestHeader.page : requestHeader.page;
      requestHeader.size = newRequestHeader.size ? newRequestHeader.size : requestHeader.size;

      if (formState.treeSelected.selectedKeys.length == 0) {//선택된 트리 값이 없을 때
        requestBody.catSeq = '';
        requestBody.catNm = '';
        requestBody.catCd = '';
        requestBody.upCatCd = '';
        requestBody.upCatNm = '';
        requestBody.printYn = '';
        requestBody.mgtYn = '';

      }
    }

    let rqHeader = cleanParam(requestHeader);
    let rqBody = cleanParam(requestBody);

    if(newRequestBody){//신규추가, 수정 이 두 상태가 한 state에 있어야 하는 관계로, 임시 처리
      rqBody = cleanParam(newRequestBody);
    }
    
    let response;

    try {

      response = await axios.post(`${apiBaseUrl}/category/getChildCategoryList`, rqBody, {params:rqHeader});
      
      let totalCnt = 0;

      if(response.data != null && response.data != ''){
        totalCnt = response.data[0].totalCnt;
        
        //setTriggerUpdate(prev => !prev);
        setReactJsPgShow(true);
      } 
      setReactJsPgListSize(totalCnt);
        
      if(reactJsPgRef.current){
        reactJsPgRef.current.updateListSize(totalCnt);
      }

      setChildCategoryList(response.data);
      
    } catch (error) {
      response = error;
    } finally {
      return response;
    }
  };

  const getParentCategoryList = async () => {//초기화 이후엔 CUD 시 수정된 항목 반영

    let response;

    try {

      response = await axios.post(`${apiBaseUrl}/category/getParentCategoryList`, {}, {});
      
      if(response.data != null && response.data != ''){
        response.data.unshift({key : '', value : '선택 없음'});

        setUpCatSelectOptions(response.data);
      }
      
    } catch (error) {
      response = error;
    } finally {
      return response;
    }
  };

  const saveCategory = async ( ) => {
    if(validation(requestBody)){
      if (formState.rowSelected.rowClickCatSeq > 0) {//로우 클릭 중
        
        console.log('트리 클릭 상태인데 나오냐');
      } else if (formState.treeSelected.selectedKeys.length > 0) {//트리 클릭 중
        if( formState.treeSelected.selectedInfo.node ){//아니 node없다고 오류남
          switch(formState.state){
            case 'i' : 
            break;
            case 'u' :
            break;
          }
          
        } else {
          requestBody.catSeq = selectedInfo.node.catSeq;
          requestBody.catCd = selectedKeys[0];
        }
      }

      const rqHeader = cleanParam(requestHeader);
      const rqBody = cleanParam(requestBody);

      let response;
      try {
        if(catSeq > 0){
          response = (await axios.put(`${apiBaseUrl}/category/put`, rqBody, {params:rqHeader}));
        } else {
          response = (await axios.post(`${apiBaseUrl}/category/post`, rqBody, {params:rqHeader}));
        }

        alert("카테고리 업데이트 완료");
      } catch (error) {
        response = error;
        alert("카테고리 업데이트 실패\n"+response.response.data.message);
      } finally {
        onCancelClick();
        return response;
      }
    }
  };

  const deleteCategory = async ( ) => {
    if(validation(requestBody)){
      const rqHeader = cleanParam(requestHeader);
      const rqBody = cleanParam(requestBody);
  
      let response;
      try {
        response = (await axios.put(`${apiBaseUrl}/category/delete`, rqBody, {params:rqHeader}));
        
        alert("카테고리 삭제 완료");
      } catch (error) {
        response = error;
        alert("카테고리 삭제 실패\n"+response.response.data.message);
      } finally {
        onCancelClick();
        return response;
      }
    }
  };

  const stateClear = function (){
    
    setFormStateStr('i');
    formState.state = 'i';

    setSelectedKeys([]);
    setSelectedInfo(null);
    formState.treeSelected.selectedKeys = '';
    formState.treeSelected.selectedInfo = '';

    setRowClickCatCd('');
    setRowClickCatSeq('');
    formState.rowSelected.rowClickCatSeq = '';
    formState.rowSelected.rowClickCatCd = '';

    setCatSeq('');

    setShowDelBtn(false);
    setShowEditBtn(false);
    setShowEditCancelBtn(false);

    setReactJsPgListSize(0);
    setReactJsPgShow(false);

    setChildCategoryList([]);
  }

  const inputClear = function(){

    setCatSeq('');
    setInputCatNm('');
    setInputCatCd('');
    setInputUpCatCd('');
    setInputUpCatNm('');
    setInputPrintYn('y');
    setInputMgtYn('y');
    requestBody.catSeq = '';
    requestBody.catNm = '';
    requestBody.catCd = '';
    requestBody.upCatCd = '';
    requestBody.upCatNm = '';
    requestBody.printYn = '';
    requestBody.mgtYn = '';
  }

  const inputSet = function (category){
    switch(formState.state){
      
      case 'i' :
        if (category.catCd || category.catCd == '') {
          setInputUpCatCd(category.catCd);
          requestBody.upCatCd = category.catCd;
        }
    
        if (category.catNm) {
          setInputUpCatNm(category.catNm);
          requestBody.upCatNm = category.catNm;
        }

        
      break;
      case 'u' : 
      //위에 i 에서 수정한 upCatCd, nm을 진짜놈으로 바꿔야함. 위에선 자기자신을 칭함.
        if (category.catSeq) {
          setCatSeq(category.catSeq);
          requestBody.catSeq = category.catSeq;
        }
        if (category.catNm) {
          setInputCatNm(category.catNm);
          requestBody.catNm = category.catNm;
        }
    
        if (category.catCd) {
          setInputCatCd(category.catCd);
          requestBody.catCd = category.catCd;
        }
    
        if (category.upCatCd || category.upCatCd == '') {
          setInputUpCatCd(category.upCatCd);
          requestBody.upCatCd = category.upCatCd;
        }
    
        if (category.upCatNm) {
          setInputUpCatNm(category.upCatNm);
          requestBody.upCatNm = category.upCatNm;
        }
    
        if (category.printYn) {
          setInputPrintYn(category.printYn);
          requestBody.printYn = category.printYn;
        }
        
        if (category.mgtYn) {
          setInputMgtYn(category.mgtYn);
          requestBody.mgtYn = category.mgtYn;
        }
      break;
      default : 

      break;
    }
  }

  const search = function(e){

    setSelectSearchKey(selectSearchKey); 
    setInputSearchStr(inputSearchStr); 
    requestHeader.searchKey = selectSearchKey;
    requestHeader.searchStr = inputSearchStr;

    //페이지네이션 초기화
    if(reactJsPgRef.current){
      reactJsPgRef.current.updateActivePage();
    }

    getChildCategoryList();
  }

  const searchReset = function(e){

    setSelectSearchKey(''); 
    setInputSearchStr(''); 
    requestHeader.searchKey = '';
    requestHeader.searchStr = '';

    //페이지네이션 초기화
    if(reactJsPgRef.current){
      reactJsPgRef.current.updateActivePage();
    }

    getChildCategoryList();

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
    return true;
  }

  const onTreeSelect = (selectedKeys, info, d,e,f) => {

    //init
    stateClear();
    inputClear();
    
    if(selectedKeys.length > 0){

      setSelectedKeys(selectedKeys);
      setSelectedInfo(info);
      formState.treeSelected.selectedKeys = selectedKeys;
      formState.treeSelected.selectedInfo = info;

      let category = {
        catSeq : info.node.catSeq
        , catCd : selectedKeys[0]
        , catNm : info.node.title
        , upCatCd : info.node.upCatCd == null ? '' : info.node.upCatCd
        , upCatNm : info.node.upCatNm
        , printYn : info.node.printYn//하위 리스트 조회문때매 주석한듯 수정중
        , mgtYn : info.node.mgtYn//하위 리스트 조회문때매 주석한듯 수정중
      }
      inputSet(category);

      setShowEditBtn(true);
      setShowEditCancelBtn(false);
      setShowDelBtn(false);
    } 

    searchReset();
    
  };

  const onRowClick = function(selectedObj){

    //init
    inputClear();

    setSelectSearchKey(''); 
    setInputSearchStr(''); 
    requestHeader.searchKey = '';
    requestHeader.searchStr = '';

    setRowClickCatSeq('');
    setRowClickCatCd('');
    formState.rowSelected.rowClickCatSeq = '';
    formState.rowSelected.rowClickCatCd = '';

    //공통
    if (rowClickCatSeq == selectedObj.catSeq) {
        
      onEditCancelClick();

    } else {
      setFormStateStr('u');
      formState.state = 'u';
      setRowClickCatSeq(selectedObj.catSeq);
      setRowClickCatCd(selectedObj.catCd);
      formState.rowSelected.rowClickCatSeq = selectedObj.catSeq;
      formState.rowSelected.rowClickCatCd = selectedObj.catCd;
      inputSet(selectedObj);
      setShowDelBtn(true);
      setShowEditBtn(false);
      setShowEditCancelBtn(true);
    }
  }

  const inputSearchStrOnKeyUp = function(e){

    if(e.key == 'Enter'){

      search();//위에꺼 대체 가능

    }
  }
  
  const onCancelClick = function () {

    stateClear();//treeSelect에서 취소누를시 실행
    inputClear();//treeSelect에서 취소누를시 실행
    
    getCategoryListTreeList();
    getParentCategoryList();
    
    searchReset();
  }

  const onEditClick = function(){

    setFormStateStr('u');
    formState.state = 'u';

    let category = {
      catSeq : selectedInfo.node.catSeq
      , catCd : selectedKeys[0]
      , catNm : selectedInfo.node.title
      , upCatCd : selectedInfo.node.upCatCd == null ? '' : selectedInfo.node.upCatCd
      , upCatNm : selectedInfo.node.upCatNm
      , printYn : selectedInfo.node.printYn
      , mgtYn : selectedInfo.node.mgtYn
    }

    inputSet(category);
    setShowDelBtn(true);
    setShowEditBtn(false);
    setShowEditCancelBtn(true);
  }

  const onEditCancelClick = function(){

    setFormStateStr('i');//treeSelect에서 수정 취소누를시
    formState.state = 'i';//treeSelect에서 수정 취소누를시

    setRowClickCatCd('');
    setRowClickCatSeq('');
    formState.rowSelected.rowClickCatSeq = '';
    formState.rowSelected.rowClickCatCd = '';
    
    setCatSeq('');
    
    setShowDelBtn(false);
    setShowEditBtn(true);
    setShowEditCancelBtn(false);
    
    inputClear();
    inputSet({catCd : selectedKeys ? selectedKeys[0] : '', catNm : selectedInfo ? selectedInfo.node.title : ''});
    
  }

  const onSelectChange = function(e){
    if(e.target.value == null){
      setInputUpCatCd('');
    } else{ 
      setInputUpCatCd(e.target.value);
    }
  }

  return ( 
    <div className='root_box admin'>
      <div className='cont'>
        <Header title='카테고리 설정'/>
        <div className='contents_box'>
          <div className='content_box category_box'>
            <div className='content_header'>트리뷰</div>
            <CategoryListTree onClick={onTreeSelect} selectedKeys={selectedKeys} mgtYn={treeMgtYn} treeData={categoryListTreeList}/>
          </div>
          <div className='dnm_content'>
            <div className='content_box cat_in'>
                <div className='content_header'>카테고리 신규 생성
                    <div className='button_box'>
                        <BoardButton type="cancel" color="white" onClick={onCancelClick}/>
                        <BoardButton type="cancel_put" color="white" show={showEditCancelBtn} onClick={onEditCancelClick}/>
                        <BoardButton type="put" color="white" show={showEditBtn} onClick={onEditClick}/>
                        <BoardButton type="save" color="orange" onClick={saveCategory}/>
                        <BoardButton type="delete" color="white" show={showDelBtn} onClick={deleteCategory}/>
                    </div>
                </div>
                <div className='input_container_box'>
                    <Input type = 'hidden' value={catSeq} onChange={(event)=>{setCatSeq(event.target.value);} } />
                    <Input type = 'text' value={inputCatNm} onChange={(event)=>{setInputCatNm(event.target.value);}} label_str = '카테고리명' placeholder_str='카테고리명을 입력하세요.'/>
                    <Input type = 'text' value={inputCatCd} onChange={(event)=>{setInputCatCd(event.target.value);}} label_str = '코드' placeholder_str='코드를 입력하세요.'/>
                </div>
                <div className='input_container_box'>
                    <div className="input_container select"><span className='ol_dot'/><label>상위 카테고리</label><Select value={inputUpCatCd} options = {upCatSelectOptions} onChange = {(e)=>{onSelectChange(e)}}/></div>
                    <Input type = 'checkbox' isChecked={inputPrintYn == 'y' ? true : false} label_str = '출력대상여부' imgs = {props.imgs} onChange = {(event)=>{setInputPrintYn(inputPrintYn == 'y' ? 'n' : 'y');}}/> 
                    <Input type = 'checkbox' isChecked={inputMgtYn == 'y' ? true : false} label_str = '관리항목유무' imgs = {props.imgs} onChange = {(event)=>{setInputMgtYn(inputMgtYn == 'y' ? 'n' : 'y');}}/>
                </div>
            </div>
            <div className='content_box cat_sel'>
                <div className='content_header'><span className='title'>하위 카테고리명</span></div>
                <div className='search_box_parent'>
                    <SearchBox  searchSelectValue={selectSearchKey} searchTextValue={inputSearchStr} searchTextOnChange={(e)=>{setInputSearchStr(e.target.value);}} searchSelectOnChange={(e)=>{setSelectSearchKey(e.target.value);}} searchTextOnKeyUp={(e)=>{inputSearchStrOnKeyUp(e)}} options={selectOptions} placeholder_str='검색옵션 선택 후 검색어를 입력하세요.' search={search} reset={searchReset} disabled={formStateStr == 'u' ? true : false}/>
                </div>
                <BoardList type="DtAttach" placeholder_str='검색옵션 선택 후 검색어를 입력하세요.' thead={[{key : 'catCd', value : '카테고리 코드'},{key : 'catNm', value : '카테고리 이름'}]} tbody={ChildCategoryList} onRowClick={onRowClick} selectedId={rowClickCatSeq} />
                <div className='pagenation_box'>
                    <div className='label'>총 카운트 {reactJsPgListSize}</div>
                    <div className={reactJsPgShow ? 'pg show' : 'pg hide'}>
                      <ReactJsPg ref={reactJsPgRef} getList={getChildCategoryList} requestHeader={requestHeader} requestBody={requestBody} rowClick={onRowClick} editCancelClick={onEditCancelClick} rowClickCatSeq={rowClickCatSeq} selectedKeys={selectedKeys} selectedInfo={selectedInfo}/>
                      
                    </div>
                </div>
            </div>
        </div>
        </div>
      </div>
    </div>
  );
}

export default Container;


class ReactJsPg extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      activePage: 1,
      listSize : 0,
      pageSize : 10
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
          , page: this.state.activePage  // 새로운 activePage로 업데이트
          , size: this.state.pageSize    // 현재 페이지 크기를 사용
        };

        if(this.props.requestBody.upCatCd){
          let updatedRequestBody = {
            catSeq : this.props.selectedInfo.node.catSeq
            , catCd : this.props.selectedKeys[0]
            , catNm : this.props.selectedInfo.node.title
            , upCatCd : this.props.selectedInfo.node.upCatCd == null ? '' : this.props.selectedInfo.node.upCatCd
            , upCatNm : this.props.selectedInfo.node.upCatNm
          }
          this.props.getList(updatedHeader, updatedRequestBody);
        } else {
          this.props.getList(updatedHeader);
        }
  
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
        hideDisabled={false}
      />
    );
  }
}