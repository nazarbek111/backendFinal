import React, { useState } from 'react';
import api from '../api/axios';
import { useNavigate } from 'react-router-dom';

const Login = () => {
    const [email, setEmail] = useState('nazarbek.kaliyev@gmail.com');
    const [password, setPassword] = useState('060106');
    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();
        console.log("Пытаюсь зайти с:", { email, password });
        try {
            const res = await api.post('/auth/login', { email, password });
            console.log("Ответ бэка:", res.data);

            window.location.href = '/child';

            // Сохраняем токен (проверяем оба варианта названия)
            const token = res.data.accessToken || res.data.token;
            localStorage.setItem('token', token);

            alert("Успешный вход! 🎉");
            navigate('/child');
        } catch (err) {
            console.error("Ошибка при логине:", err.response?.data || err.message);
            alert("Ошибка: " + (err.response?.data?.message || "Неверный логин или пароль"));
        }
    };

    return (
        <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh', background: '#38bdf8' }}>
            <div style={{ background: 'white', padding: '40px', borderRadius: '24px', boxShadow: '0 10px 25px rgba(0,0,0,0.2)', width: '350px', textAlign: 'center' }}>
                <h2 style={{ marginBottom: '20px', color: '#1e293b' }}>ВХОД В LITERACY.AI</h2>
                <form onSubmit={handleLogin}>
                    <input
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        placeholder="Email"
                        style={{ width: '100%', padding: '12px', marginBottom: '10px', borderRadius: '8px', border: '1px solid #ccc' }}
                    />
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        placeholder="Пароль"
                        style={{ width: '100%', padding: '12px', marginBottom: '20px', borderRadius: '8px', border: '1px solid #ccc' }}
                    />
                    <button type="submit" style={{ width: '100%', padding: '12px', background: '#f97316', color: 'white', border: 'none', borderRadius: '8px', fontWeight: 'bold', cursor: 'pointer' }}>
                        ПОЕХАЛИ! 🚀
                    </button>
                </form>
            </div>
        </div>
    );
};

export default Login;