import { useState } from "react";
import { useForm } from "react-hook-form";
import { useNavigate, Link } from "react-router-dom";
import api from "../api/axios";

export default function Register() {
    const navigate = useNavigate();
    const [serverError, setServerError] = useState("");
    const [success, setSuccess] = useState("");

    const {
        register,
        handleSubmit,
        formState: { errors, isSubmitting },
    } = useForm();

    const submit = async (data) => {
        setServerError("");
        setSuccess("");

        try {
            await api.post("/auth/register", {
                name: data.name,
                email: data.email,
                password: data.password,
            });

            setSuccess("Account created successfully. Redirecting to login...");
            setTimeout(() => navigate("/login"), 800);
        } catch (error) {
            setServerError(
                error.response?.data?.message ||
                error.response?.data?.error ||
                "Registration failed. Check backend or API endpoint."
            );
        }
    };

    return (
        <div className="min-h-screen bg-gradient-to-br from-green-100 via-blue-100 to-yellow-100 flex items-center justify-center p-6">
            <form
                onSubmit={handleSubmit(submit)}
                className="w-full max-w-md bg-white rounded-3xl shadow-xl p-8 space-y-4"
            >
                <h1 className="text-3xl font-bold text-center text-slate-800">
                    Create account
                </h1>

                {serverError && (
                    <div className="bg-red-100 text-red-700 p-3 rounded-xl">
                        {serverError}
                    </div>
                )}

                {success && (
                    <div className="bg-green-100 text-green-700 p-3 rounded-xl">
                        {success}
                    </div>
                )}

                <div>
                    <input
                        className="w-full border rounded-xl p-3"
                        placeholder="Parent name"
                        {...register("name", { required: "Name is required" })}
                    />
                    {errors.name && (
                        <p className="text-red-500 text-sm mt-1">{errors.name.message}</p>
                    )}
                </div>

                <div>
                    <input
                        className="w-full border rounded-xl p-3"
                        placeholder="Email"
                        type="email"
                        {...register("email", {
                            required: "Email is required",
                            pattern: {
                                value: /^\S+@\S+\.\S+$/,
                                message: "Enter valid email",
                            },
                        })}
                    />
                    {errors.email && (
                        <p className="text-red-500 text-sm mt-1">{errors.email.message}</p>
                    )}
                </div>

                <div>
                    <input
                        className="w-full border rounded-xl p-3"
                        placeholder="Password"
                        type="password"
                        {...register("password", {
                            required: "Password is required",
                            minLength: {
                                value: 6,
                                message: "Password must be at least 6 characters",
                            },
                        })}
                    />
                    {errors.password && (
                        <p className="text-red-500 text-sm mt-1">
                            {errors.password.message}
                        </p>
                    )}
                </div>

                <button
                    disabled={isSubmitting}
                    className="w-full bg-green-500 text-white rounded-xl p-3 font-bold hover:bg-green-600 disabled:opacity-60"
                >
                    {isSubmitting ? "Creating..." : "Register"}
                </button>

                <p className="text-center text-sm">
                    Already have account?{" "}
                    <Link className="text-blue-600 font-semibold" to="/login">
                        Login
                    </Link>
                </p>
            </form>
        </div>
    );
}