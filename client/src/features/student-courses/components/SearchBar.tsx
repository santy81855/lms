import type { Dispatch, SetStateAction } from "react";
import styles from "./SearchBar.module.css";

type SearchContentProp = {
    searchContent : string,
    setSearchContent : Dispatch<SetStateAction<string>>
};

function SearchBar( {searchContent, setSearchContent} : SearchContentProp ){
    function onChange(evt : React.ChangeEvent<HTMLInputElement>){
        setSearchContent(evt.target.value);
    }

    return (
        <>
        <input 
            className={styles.searchBar} 
            type="text" 
            onChange={onChange}
            value={searchContent}
            placeholder={"Search for a course name, description, or subject"}
            />
        </>
    );
}

export default SearchBar;