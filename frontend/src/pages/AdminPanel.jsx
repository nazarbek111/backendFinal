import {
    useEffect,
    useState
} from "react";

import api from "../api/axios";

export default function AdminPanel(){

    const[
        users,
        setUsers
    ]=useState([])

    useEffect(()=>{

        api
            .get(
                "/admin/stats"
            )
            .then(
                res=>setUsers(
                    res.data
                )
            )

    },[])

    return(

        <div>

            <h1>

                Admin Panel

            </h1>

            <pre>

{
    JSON.stringify(
        users,
        null,
        2
    )
}

</pre>

        </div>

    )

}