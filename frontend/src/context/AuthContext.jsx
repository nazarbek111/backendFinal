import { createContext, useContext, useEffect, useState } from "react";
import api from "../api/axios";

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(() => {
        const savedUser = localStorage.getItem("user");
        return savedUser ? JSON.parse(savedUser) : null;
    });

    const [loading, setLoading] = useState(false);

    const login = async (email, password) => {
        const res = await api.post("/auth/login", {
            email,
            password,
        });

        const accessToken = res.data.accessToken || res.data.token;
        const loggedUser =
            res.data.user || {
                id: res.data.id,
                name: res.data.name,
                email: res.data.email,
                role: res.data.role || "PARENT",
            };

        if (accessToken) {
            localStorage.setItem("token", accessToken);
        }

        localStorage.setItem("user", JSON.stringify(loggedUser));
        setUser(loggedUser);

        return loggedUser;
    };

    const logout = async () => {
        try {
            await api.post("/auth/logout");
        } catch {
            // ignore logout errors
        }

        localStorage.removeItem("token");
        localStorage.removeItem("user");
        setUser(null);
    };

    useEffect(() => {
        setLoading(false);
    }, []);

    return (
        <AuthContext.Provider
            value={{
                user,
                login,
                logout,
                loading,
            }}
        >
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);