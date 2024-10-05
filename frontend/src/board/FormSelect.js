import React, {useEffect, useState} from 'react';

function FormSelect ( props ) {

    return (
        <select value={props.value} onChange={props.onChange} disabled={props.disabled}>
            {props.options.map((content, i) => (
                <option key={i} value={content['key']}>{content['value']}</option>
            ))}
        </select>
    );
    
}

export default FormSelect;