import {
    useEffect,
    useState
} from "react";

import api from "../api/axios";

export default function ParentDashboard(){

    const[
        stats,
        setStats
    ]=useState(null)

    useEffect(()=>{

        api
            .get("/parents/me")
            .then(
                res=>setStats(
                    res.data
                )
            )

    },[])

    if(!stats){

        return(
            <p>
                Loading...
            </p>
        )

    }

    return(

        <div>

            <h1>

                Dashboard

            </h1>

            <p>

                XP:
                {stats.totalXp}

            </p>

            <p>

                Streak:
                {stats.streak}

            </p>

        </div>

    )

}