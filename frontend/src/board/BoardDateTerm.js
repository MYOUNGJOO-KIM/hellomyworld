import React from 'react';

import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import { ko } from 'date-fns/locale';



function BoardDateTerm ( props ) {
    
    return (
        <div className='date_term_box datepicker'>

            <DatePicker className={props.disabled ? "icon disabled" : "icon"} selected={props.searchStdValue} onChange={(date)=>{props.searchStdOnChange(date)}} dateFormat="yyyy-MM-dd" locale={ko} placeholderText='연월일을 선택하세요.'/>
            <span>~</span> 
            <DatePicker className={props.disabled ? "icon disabled" : "icon"} selected={props.searchEdValue} onChange={(date)=>{props.searchEdOnChange(date)}} dateFormat="yyyy-MM-dd" locale={ko} placeholderText='연월일을 선택하세요.'/>
           
        </div>
    );
}

export default BoardDateTerm;