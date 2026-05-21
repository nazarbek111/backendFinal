import {
    BrowserRouter,
    Routes,
    Route
} from "react-router-dom";

import Landing from "./pages/Landing";
import Login from "./pages/Login";
import Register from "./pages/Register";
import ChildSelector from "./pages/ChildSelector";
import ChildMap from "./pages/ChildMap";
import ParentDashboard from "./pages/ParentDashboard";
import AdminPanel from "./pages/AdminPanel";

import ProtectedRoute from "./components/ProtectedRoute";

function App(){

    return(

        <BrowserRouter>

            <Routes>

                <Route
                    path="/"
                    element={<Landing/>}
                />

                <Route
                    path="/login"
                    element={<Login/>}
                />

                <Route
                    path="/register"
                    element={<Register/>}
                />

                <Route
                    path="/children"
                    element={
                        <ProtectedRoute>

                            <ChildSelector/>

                        </ProtectedRoute>
                    }
                />

                <Route
                    path="/map"
                    element={
                        <ProtectedRoute role="CHILD">
                            <ChildMap/>
                        </ProtectedRoute>
                    }
                />

                <Route
                    path="/dashboard"
                    element={
                        <ProtectedRoute role="PARENT">
                            <ParentDashboard/>
                        </ProtectedRoute>
                    }
                />

                <Route
                    path="/admin"
                    element={
                        <ProtectedRoute role="ADMIN">
                            <AdminPanel/>
                        </ProtectedRoute>
                    }
                />

            </Routes>

        </BrowserRouter>

    )

}

export default App