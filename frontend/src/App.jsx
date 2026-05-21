import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';

import Login from './pages/Login';
import ChildMap from './pages/ChildMap';
import Exercise from './pages/Exercise';
import ParentDashboard from './pages/ParentDashboard';

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/login" element={<Login />} />
                <Route path="/child" element={<ChildMap />} />
                <Route path="/parent/stats" element={<ParentDashboard />} />

                {/* Перенаправление с главной на логин */}
                <Route path="/" element={<Navigate to="/login" />} />
                <Route path="/exercise/:id" element={<Exercise />} />

                {/* Обработка несуществующих страниц */}
                <Route path="*" element={<div style={{padding: '20px'}}><h1>404: Страница не найдена 🦖</h1></div>} />
            </Routes>
        </Router>
    );
}

export default App;