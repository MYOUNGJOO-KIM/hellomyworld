import React, {useEffect, useState} from 'react';
import BoardList from '../board/BoardList';
import SearchBox from "./../board/SearchBox";
import icon_x_white from './../assets/images/icon_x_white.svg';
import { CategoryContext, useCategoryContext } from '../CategoryContexts';
import axios from 'axios';

function PreviewPopUp (props) {
    const apiBaseUrl = process.env.REACT_APP_API_BASE_URL;
    
    const [attachFileList, setAttachFileList] = useState([]);
    const { cleanParam } = useCategoryContext();
    const [ChildCategoryList, setChildCategoryList] = useState([]);
    const [selectSearchKey, setSelectSearchKey] = useState('');//select
    const [inputSearchStr, setInputSearchStr] = useState('');//text
    const [reactJsPgListSize, setReactJsPgListSize] = useState(0);
    
    

    // const getAttachFilePathList = async () => {
    //     let response;
    //     try {
    //         response = await axios.post(`${apiBaseUrl}/attachment/getAttachFilePathList`, requestBody.attachSeqList, {
    //             params : requestHeader
    //         });
    //         setChildCategoryList(response.data);
            
    //     } catch (error) {
    //         response = error;
    //     } finally {
    //         return response;
    //     }
    // };
 
    const getChildCategoryAttachList = async () => {
        const rqHeader = cleanParam(requestHeader);
        let requestBody = {};

        if(rqHeader.catCd){
            requestBody.catCd = rqHeader.catCd;
            delete rqHeader.catCd;
        }
        
        let response;

        try {
            response = await axios.post(`${apiBaseUrl}/attachment/getChildCategoryAttachList`, requestBody, {params : rqHeader});
            
            if(response.data != null && response.data != ''){
                setReactJsPgListSize(response.data[0].totalCnt);
            }
            setChildCategoryList(response.data);
            
        } catch (error) {
            response = error;
        } finally {
            return response;
        }
    };
    
    useEffect(() => {
        //getAttachFilePathList();
        getChildCategoryAttachList();
    }, []);


    const urlParams = new URLSearchParams(window.location.search);
    //const encodedInfo = urlParams.get('requestHeader'); // "2%2C1%2C3%2C4%2C5%2C7%2C6%2C9%2C8"
    //const decodedInfo = JSON.parse(encodedInfo);
    //decodeURIComponent(encodedInfo).split(',').map(item => Number(item.trim())); // "2,1,3,4,5,7,6,9,8"
    //console.log(decodedInfo);

    const requestHeader = JSON.parse(urlParams.get('requestHeader'));

    const requestBody = {
        attachSeqList : ''
    }

    const selectOptions = [{key : 'catNm', value : '카테고리 이름'}, {key : 'catCd', value : '카테고리 코드'}];

    return(
        
        <div className='pop_up a4 preview_pop_up'  width="706px" height="1000px">
            <div className="pop_up_child">
                {ChildCategoryList.map((attach, i) => (
                    <div key={i}>
                        <img src={attach.attachFilePath}></img>
                        <p>{attach.attachFilePath}</p>
                    </div>
                ))}
            </div>
        </div>
    )
}

export default PreviewPopUp;