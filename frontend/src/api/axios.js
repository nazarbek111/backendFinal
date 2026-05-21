import axios from "axios";

const api = axios.create({
    baseURL: import.meta.env.VITE_API_URL,
    withCredentials:true
});

api.interceptors.request.use((config)=>{
    const token=localStorage.getItem("token");

    if(token){
        config.headers.Authorization=`Bearer ${token}`
    }

    return config
})


api.interceptors.response.use(
    response=>response,

    async(error)=>{

        const original=error.config;

        if(
            error.response?.status===401 &&
            !original._retry
        ){

            original._retry=true

            try{

                const res=await axios.post(
                    `${import.meta.env.VITE_API_URL}/auth/refresh`,
                    {},
                    {
                        withCredentials:true
                    })

                const token=res.data.accessToken;

                localStorage.setItem(
                    "token",
                    token
                )

                original.headers.Authorization=
                    `Bearer ${token}`

                return api(original)

            }catch{

                localStorage.removeItem(
                    "token"
                )

                window.location="/login"
            }
        }

        return Promise.reject(error)

    }
)

export default api