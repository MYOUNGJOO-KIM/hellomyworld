import React from 'react';

import icon_btnDelete from './../assets/images/icon_btnDelete.png';
import icon_btnModify from './../assets/images/icon_btnModify.png';
import icon_btnRegist from './../assets/images/icon_btnRegist.png';
import icon_search from './../assets/images/icon_search.png';
import icon_refresh from './../assets/images/icon_refresh.svg';
import classNames from 'classnames';


function BoardButton ( props ) {


    switch (props.type) {
        case 'get' :
            return (
                <button className={`get`} onClick={(e)=>props.onClick(props.data)} disabled={props.disabled}><img src={(icon_search)}/>검색</button>
            );
        case 'post' :
            return (
                <button className={`post`} disabled={props.disabled}><img src={(icon_btnRegist)}/>등록</button>
            );

        case 'put' :
            return (
                <button className={classNames({'hidden' : props.show == false, 'put' : props.show == true})} onClick={(e)=>props.onClick(props.data)} disabled={props.disabled}><img src={(icon_btnModify)}/>수정</button>
            );

        case 'delete' :
            return (
                <button className={classNames({'hidden' : props.show == false, 'delete' : props.show == true})} onClick={()=>props.onClick(props.data)} disabled={props.disabled}><img src={(icon_btnDelete)}/>삭제</button>
            );
        case 'save' :
            return (
                <button className={`save`} onClick={()=>props.onClick(props.data, props.validation)} disabled={props.disabled}><img src={(icon_btnRegist)}/>저장</button>
            );
        case 'cancel' :
            return (
                <button className={classNames({'hidden' : props.show == false, 'cancel' : props.show == true})} onClick={()=>props.onClick(props.data, props.validation)} disabled={props.disabled}>취소</button>
            );
        case 'cancel_put' :
            return (
                <button className={classNames({'hidden' : props.show == false, 'cancel' : props.show == true})} onClick={()=>props.onClick(props.data, props.validation)} disabled={props.disabled}>수정 취소</button>
            );
        case 'reset' :
            return (
                <button className={`reset`} onClick={props.onClick} disabled={props.disabled}><img src={(icon_refresh)}/></button>
            );
        case 'dtAttachPrintAll' :
            return (
                <button className={classNames({'hidden' : props.show == false, 'dt_attach_print_all' : props.show == true})} onClick={(e)=>props.onClick(props.data)} disabled={props.disabled}>일괄출력</button>
            );

        default :
            return (
                <button className={`save`} onClick={props.onClick} disabled={props.disabled}><img src={(icon_btnRegist)}/>저장</button>
            );
    }
    
}

export default BoardButton;

