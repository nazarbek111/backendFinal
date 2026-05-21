import {useEffect,useState} from "react";
import api from "../api/axios";

export default function ChildSelector(){

    const[
        children,
        setChildren
    ]=useState([])

    useEffect(()=>{

        api
            .get("/children")
            .then(
                res=>setChildren(
                    res.data
                )
            )

    },[])

    return(

        <div>

            {children.map(
                child=>(

                    <div
                        key={child.id}
                        className="p-4 rounded-xl"
                    >

                        <img
                            src={child.avatar}
                        />

                        <h2>

                            {child.name}

                        </h2>

                    </div>

                )
            )}

        </div>

    )

}