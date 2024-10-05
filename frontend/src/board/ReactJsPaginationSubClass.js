import React from 'react';
import ReactJsPagination from "react-js-pagination";

class ReactJsPaginationSubClass extends React.Component {
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
            // , printYn : this.props.selectedInfo.node.printYn
            // , mgtYn : this.props.selectedInfo.node.mgtYn
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
          //test중. 2개 이상일 때 보이는 지
          hideDisabled={true}
        />
      );
    }
  }

export default ReactJsPaginationSubClass;