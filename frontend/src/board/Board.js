import React from 'react';

import { useCategoryContext } from '../CategoryContexts';

function Board ( props ){
    const { data, loading, error } = useCategoryContext();

    if (loading) {
        return <p>Loading...</p>;
    }

    if (error) {
        return <p>Error: {error.message}</p>;
    }

    let thead = []; 
    let tbody = [];

    tbody = data;

    if(tbody && tbody.length > 0){
        Object.keys(tbody[0]).map((key, i, arr) => {//dynamic header select
            thead.push(key);
        });
    }

    const createTbody = () => {
        const table = [];

        tbody.forEach(row => {
            thead.forEach(cell => {
                table.push(<td className={'n'+thead.length}>{row.cell}</td>);
            });
        });
        return table;
    }

    return (
    <div className='board'>
        <table>
            <thead>
                <tr>
                    {thead.map((header, i) => (
                        <th key={i} className={'n'+thead.length}>{header}</th>
                    ))}
                </tr>
            </thead>
            <tbody id="BoardListTable">
            </tbody>
        </table>
    
    </div>
    )
    
}


export default React.memo(Board);
