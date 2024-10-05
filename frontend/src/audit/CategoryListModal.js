import React, {useEffect, useRef, useState} from 'react';
import BoardList from '../board/BoardList';
import SearchBox from "./../board/SearchBox";
import icon_x_white from './../assets/images/icon_x_white.svg';
import axios from 'axios';
import ReactJsPagination from "react-js-pagination";
import { CategoryContext, useCategoryContext } from '../CategoryContexts';


function CategoryListModal (props) {
    const [reactJsPgListSize, setReactJsPgListSize] = useState(0);
    const reactJsPgRef = useRef(null);

    const apiBaseUrl = process.env.REACT_APP_API_BASE_URL;
    const { cleanParam } = useCategoryContext();
    const [ChildCategoryList, setChildCategoryList] = useState([]);
    const [selectSearchKey, setSelectSearchKey] = useState('');//select
    const [inputSearchStr, setInputSearchStr] = useState('');//text
    
    const requestHeader = {
        searchKey : selectSearchKey
        , searchStr : inputSearchStr
    };

    const thead=[{key : 'catCd', value : '카테고리 코드'},{key : 'catNm', value : '카테고리 이름'}] ;
    
    const getChildCategoryList = async (newRequestHeader, newRequestBody) => {

        if(newRequestHeader){
            requestHeader.searchKey = newRequestHeader.searchKey ? newRequestHeader.searchKey : requestHeader.searchKey;
            requestHeader.searchStr = newRequestHeader.searchStr ? newRequestHeader.searchStr : requestHeader.searchStr;
            requestHeader.page = newRequestHeader.page ? newRequestHeader.page : requestHeader.page;
            requestHeader.size = newRequestHeader.size ? newRequestHeader.size : requestHeader.size;
        }

        const rqHeader = cleanParam(requestHeader);

        let response;

        try {
            response = await axios.post(`${apiBaseUrl}/category/getChildCategoryList`, {}, {
                params : rqHeader
            });
            if(response.data != null && response.data != ''){
                setReactJsPgListSize(response.data[0].totalCnt);
                
                if(reactJsPgRef.current){
                  reactJsPgRef.current.updateListSize(response.data[0].totalCnt);
                }
              }
            setChildCategoryList(response.data);
            
        } catch (error) {
            response = error;
        } finally {
            return response;
        }
    };
    
    useEffect(() => {

        if(props.isOpen){
            setSelectSearchKey(props.searchKey);
            setInputSearchStr(props.searchStr);
            requestHeader.searchKey = props.searchKey;
            requestHeader.searchStr = props.searchStr;
            search();
        }

    }, [props.isOpen, props.searchKey, props.searchStr]);

    if (!props.isOpen) return null;

    const searchSelectOnChange = function(e){
        setSelectSearchKey(e.target.value);

        console.log(e.target.value);
    }

    const searchTextOnChange = function(e){
        setInputSearchStr(e.target.value);
        
        console.log(e.target.value);
    }

    const search = function(e){
        getChildCategoryList();
    }

    const searchReset = function(e){
        requestHeader.searchKey = '';
        requestHeader.searchStr = '';
        setSelectSearchKey(''); 
        setInputSearchStr(''); 
        getChildCategoryList();
    }

    const searchTextOnKeyUp = function(e){
        if(e.key === 'Enter'){
            getChildCategoryList()
        }
    }

    const selectOptions = [{key : 'catNm', value : '카테고리 이름'}, {key : 'catCd', value : '카테고리 코드'}];

    return(
        <div className='modal category_list_modal'>
            <div className="modal_child">
                <div className="header">
                    <div className="x" onClick={props.onClose}><img src={icon_x_white}/></div>
                </div>
                <div className='search_box_parent'>
                    <SearchBox options = {selectOptions} searchTextOnChange={searchTextOnChange} searchSelectOnChange={searchSelectOnChange} searchTextOnKeyUp={(e)=>{searchTextOnKeyUp(e)}} search={search} reset={searchReset} searchSelectValue={selectSearchKey} searchTextValue={inputSearchStr} placeholder_str='검색옵션 선택 후 검색어를 입력하세요.' />
                </div>
                <BoardList type="DtAttach"  placeholder_str='검색옵션 선택 후 검색어를 입력하세요.' thead={thead} tbody={ChildCategoryList} onRowClick= {props.onRowClick}/>
                <div className='pagenation_box'>
                    <div className='label'>총 카운트 {reactJsPgListSize}</div>
                    <ReactJsPg ref={reactJsPgRef} getList={getChildCategoryList} requestHeader={requestHeader}/>
                </div>
            </div>
        </div>
    )
}

export default CategoryListModal;

class ReactJsPg extends React.Component {
    constructor(props) {
      super(props);
  
      this.state = {
        activePage: 1,
        listSize : 0,
        pageSize : 10
      };
    }
  
    updateListSize = (newSize) => {
      this.setState({listSize : newSize});
    }
    
    handlePageChange(pageNumber) {
      console.log(`Active page is ${pageNumber}`);
      
      this.setState({ activePage: pageNumber }, () => {
        if (this.props.getList) {
          const updatedHeader = {
            id : this.props.requestHeader.id
            , searchKey : this.props.requestHeader.searchKey
            , searchStr : this.props.requestHeader.searchStr
            , page: this.state.activePage  // 새로운 activePage로 업데이트
            , size: this.state.pageSize    // 현재 페이지 크기를 사용
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
          hideDisabled={true}
        />
      );
    }
  }