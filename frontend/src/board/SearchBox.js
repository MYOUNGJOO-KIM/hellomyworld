import React from 'react';

import Select from './FormSelect'
import BoardButton from './BoardButton';


function SearchBox ( props ) {

    //props.options.unshift({key : '', value : '검색옵션 선택'});
    props.options.unshift({key:'', value:'검색옵션 선택'});

    return (
        <div className="search_box">
        
            <Select onChange = {props.searchSelectOnChange} options = {props.options} value={props.searchSelectValue} disabled={props.disabled}/>
            <div className="input_container text">
                <input type="text" placeholder={
                    props.placeholder_str
                } onChange={props.searchTextOnChange} value={props.searchTextValue} onKeyUp={props.searchTextOnKeyUp} disabled={props.disabled}></input>
                <div className='button_box n2 reset'>
                    <BoardButton type="get" onClick={props.search} disabled={props.disabled}/>
                    <BoardButton type="reset" onClick={props.reset} disabled={props.disabled}/>
                </div>
                
            </div>
        
        </div>
        
    );
    
}

export default SearchBox;