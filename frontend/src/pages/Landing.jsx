import { Link } from "react-router-dom";

export default function Landing(){

    return(

        <div className="min-h-screen bg-blue-100 flex flex-col items-center justify-center">

            <h1 className="text-5xl font-bold">
                ABC Kids
            </h1>

            <p className="mt-4">
                Learn with fun
            </p>

            <div className="space-x-4 mt-8">

                <Link
                    to="/login"
                    className="bg-blue-500 text-white p-4 rounded-xl"
                >

                    Login

                </Link>

                <Link
                    to="/register"
                    className="bg-green-500 text-white p-4 rounded-xl"
                >

                    Register

                </Link>

            </div>

        </div>

    )

}