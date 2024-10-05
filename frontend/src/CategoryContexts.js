

import React, { createContext, useContext } from 'react';

const CategoryContext = createContext();

export const CategoryProvider = ({ children }) => {

  const cleanParam = (data) => {

    let rqData = JSON.parse(JSON.stringify(data))
  
    return Object.fromEntries(
      Object.entries(rqData).filter(([key, value]) => value !== '' && value != null)
    );
  };

  return (
    <CategoryContext.Provider value={{ cleanParam }}>
      {children}
    </CategoryContext.Provider>
  );
};

export const useCategoryContext = () => {
  return useContext(CategoryContext);
};



