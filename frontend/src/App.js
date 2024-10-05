
import './reset.css';

import React, {useState} from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { CategoryProvider } from './CategoryContexts';


import Container from './audit/Container';
import MgtContainer from './audit/MgtContainer';
import FormContainer from './audit/FormContainer';
import DtAttachContainer from './audit/DtAttachContainer';
import PreviewPopUp from './audit/PreviewPopUp';
import styled from 'styled-components';//공용 스타일 컴포넌트


function App() {

  const propsForContainer = {};

  return (
    <CategoryProvider>
      <AppBox className="app_box" width={`${window.innerWidth}px`} height={`${window.innerHeight}px`} fontSize="14px">{/* width="1670px" height="900px" */}
        <BrowserRouter>
          <Routes>
            <Route path="/" element={<Container {...propsForContainer}/>}></Route>
            <Route path="/mgt" element={<MgtContainer {...propsForContainer}/>}></Route>
            <Route path="/form" element={<FormContainer {...propsForContainer}/>}></Route>
            
            <Route path="/dt/sapa/attach" element={<DtAttachContainer {...propsForContainer}/>}></Route>
            <Route path="/dt/sapa/attach/preview" element={<PreviewPopUp {...propsForContainer}/>}></Route>
          </Routes> 
        </BrowserRouter>
      </AppBox>
    </CategoryProvider>
  );
}

export default App;

const AppBox = styled.div`
  width: ${props => props.width || '1920px'};
  height: ${props => props.height || '945px'};
  font-size: ${props => props.fontSize || '14px'};
`;
