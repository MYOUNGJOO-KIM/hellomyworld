import React from 'react';

import Tree from "rc-tree";
import "rc-tree/assets/index.css"
  
  function CategoryListTree(props){
    return (
      <div className='tree_box'>
        <Tree
          ref={props.treeRef}
          treeData={props.treeData}
          showIcon={true}
          selectable={true}
          //draggable={true}
          draggable={false}
          onSelect={props.onClick}
          selectedKeys={props.selectedKeys}
          allowDrop={() => true}
          expandAction={"doubleClick"}
        ></Tree>
      </div>
    );
}

export default CategoryListTree;