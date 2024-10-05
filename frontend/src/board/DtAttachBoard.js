import React from 'react';

function DtAttachBoard ( props ){

    let thead = props.thead; 
    let tbody = props.tbody;

    const dtConverter = function(row, theadKey) {
        if(theadKey.endsWith('Dt') && row[theadKey]){
            const date = new Date(row[theadKey]);

            // yyyy-MM-dd 형식으로 포맷
            const yyyy = date.getFullYear();
            const mm = String(date.getMonth() + 1).padStart(2, '0'); // 월은 0부터 시작하므로 +1
            const dd = String(date.getDate()).padStart(2, '0');

            return `${yyyy}-${mm}-${dd}`;
        } else {
            return row[theadKey];
        }
    }

    const onClick = function (row) {
        if(typeof props.onRowClick == 'function'){
            props.onRowClick(row)
        }
    }

    return (
    <div className='board'>
        <table>
            <thead>
                <tr>
                    {thead.map((header, i) => (//thead key, value
                        <th key={i} className={'n'+thead.length}>{header['value']}</th>
                    ))}
                </tr>
            </thead>
            <tbody id="BoardListTable">
                {tbody.map((row, rowIndex) => (
                    <tr className={props.selectedId == row.id ? 'on' : 'off'} key={rowIndex} onClick={()=>{onClick(row)}}>
                        {thead.map((cell, cellIndex) => (
                            <td key={cellIndex} className={'n' + thead.length} title={dtConverter(row, cell.key)}>{dtConverter(row, cell.key)}</td>
                        ))}
                    </tr>
                ))}
            </tbody>
        </table>
    </div>
    )
}


export default React.memo(DtAttachBoard);
