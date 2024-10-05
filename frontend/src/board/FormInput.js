import React, {useEffect, useState} from 'react';

import CkOnImg from './../assets/images/checkbox_on_orange.svg';
import CkOffImg from './../assets/images/checkbox_off_orange.svg';


function FormInput ( props ) {

    switch (props.type) {
        case 'text' ://type, label_str, placeholder_str
            return (
                <div className="input_container text"><span className='ol_dot'/><label>{props.label_str}</label><input className={props.classNm ? props.classNm : ''} type="text" placeholder={props.placeholder_str} onChange={props.onChange} value={props.value} disabled={props.disabled}></input></div>
            );

        case 'number' ://type, label_str, placeholder_str
            return (
                <div className="input_container number"><span className='ol_dot'/><label>{props.label_str}</label><input type="number" placeholder={props.placeholder_str} onChange={props.onChange} value={props.value} disabled={props.disabled}></input></div>
            );

        case 'checkbox' ://type, label_str, cnt
            return (
                <div className="input_container ckbox"> 
                    <label>
                        <img src={props.isChecked ? (CkOnImg) : (CkOffImg)}/>
                        <input type="checkbox" checked = {props.isChecked} onChange={props.onChange} disabled={props.disabled}/>{props.label_str.split(',')[0]}
                    </label>
                </div>
            );

        case 'hidden' ://type, label_str, placeholder_str
            return (
                <div className="input_container hidden"><span className='ol_dot'/><label></label><input type="hidden" onChange={props.onChange} disabled={props.disabled}></input></div>
            );
        
        default ://type, label_str, placeholder_str
            return (
                <div className="input_container text"><span className='ol_dot'/><label>{props.label_str}</label><input type="text" placeholder={props.placeholder_str} onChange={props.onChange} value={props.value} disabled={props.disabled}></input></div>
            );

    }

}

export default FormInput;

