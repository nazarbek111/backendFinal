import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../api/axios";

export default function Login() {
    const navigate = useNavigate();

    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [serverError, setServerError] = useState("");
    const [loading, setLoading] = useState(false);

    const submit = async (e) => {
        e.preventDefault();
        setServerError("");
        setLoading(true);

        try {
            const res = await api.post("/auth/login", {
                email,
                password,
            });

            const token = res.data.accessToken || res.data.token;
            const refreshToken = res.data.refreshToken;

            const user = {
                id: res.data.id,
                name: res.data.name,
                email: res.data.email || email,
                role: res.data.role || "PARENT",
            };

            if (token) localStorage.setItem("token", token);
            if (refreshToken) localStorage.setItem("refreshToken", refreshToken);
            localStorage.setItem("user", JSON.stringify(user));

            if (user.role === "ADMIN") {
                navigate("/admin");
            } else {
                navigate("/children");
            }
        } catch (error) {
            setServerError(
                error.response?.data?.message ||
                error.response?.data?.error ||
                "Login failed. Check email or password."
            );
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-gradient-to-br from-blue-100 via-purple-100 to-yellow-100 flex items-center justify-center p-6">
            <form
                onSubmit={submit}
                className="w-full max-w-md bg-white rounded-3xl shadow-xl p-8 space-y-4"
            >
                <h1 className="text-3xl font-bold text-center text-slate-800">
                    Login
                </h1>

                {serverError && (
                    <div className="bg-red-100 text-red-700 p-3 rounded-xl">
                        {serverError}
                    </div>
                )}

                <input
                    className="w-full border rounded-xl p-3"
                    placeholder="Email"
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                />

                <input
                    className="w-full border rounded-xl p-3"
                    placeholder="Password"
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />

                <button
                    disabled={loading}
                    className="w-full bg-blue-500 text-white rounded-xl p-3 font-bold hover:bg-blue-600 disabled:opacity-60"
                >
                    {loading ? "Loading..." : "Login"}
                </button>

                <p className="text-center text-sm">
                    No account?{" "}
                    <Link className="text-green-600 font-semibold" to="/register">
                        Register
                    </Link>
                </p>
            </form>
        </div>
    );
}