import React, { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import { Star, Play, Lock, Trophy, ArrowLeft, Shield } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import confetti from 'canvas-confetti';
import api from '../api/axios';

const ChildMap = () => {
    const [lessons, setLessons] = useState([]);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchLessons = async () => {
            try {
                // Запрашиваем 50, чтобы влезли все 20 уроков
                const response = await api.get('/lessons?size=50');
                console.log("Данные из API:", response.data);

                // Берем массив из content
                const rawLessons = response.data.content || [];

                // Сортируем по orderIndex
                const sortedLessons = [...rawLessons].sort((a, b) => a.orderIndex - b.orderIndex);

                setLessons(sortedLessons);
            } catch (error) {
                console.error("Ошибка при загрузке карты:", error);
            } finally {
                setLoading(false);
            }
        };
        fetchLessons();
    }, []);

    const handleStart = (lesson) => {
        if (lesson.status === 'locked') return;
        if (lesson.status === 'done') {
            confetti({ particleCount: 100, spread: 70, origin: { y: 0.8 } });
        }
        navigate(`/exercise/${lesson.id}`);
    };

    if (loading) return (
        <div className="h-screen w-full flex items-center justify-center bg-sky-400">
            <motion.div animate={{ rotate: 360 }} transition={{ repeat: Infinity, duration: 1 }}>
                <Star size={64} className="text-white fill-yellow-400" />
            </motion.div>
        </div>
    );

    return (
        <div className="min-h-screen bg-[#e0f2fe] overflow-x-hidden relative font-sans pb-20">
            {/* Декоративные облака (плавающие) */}
            <div className="fixed inset-0 pointer-events-none overflow-hidden z-0">
                <motion.div animate={{ x: [-20, 20, -20] }} transition={{ duration: 5, repeat: Infinity }} className="absolute top-20 left-[5%] text-6xl opacity-20">☁️</motion.div>
                <motion.div animate={{ x: [20, -20, 20] }} transition={{ duration: 8, repeat: Infinity }} className="absolute top-[40%] right-[5%] text-7xl opacity-20">☁️</motion.div>
                <motion.div animate={{ y: [-10, 10, -10] }} transition={{ duration: 6, repeat: Infinity }} className="absolute bottom-20 left-[15%] text-6xl opacity-20">☁️</motion.div>
            </div>

            {/* Header Stats */}
            <div className="sticky top-0 z-50 p-4">
                <div className="max-w-xl mx-auto flex justify-between items-center bg-white/80 backdrop-blur-md border-b-4 border-gray-200 rounded-[24px] p-3 shadow-xl">
                    <button onClick={() => navigate(-1)} className="p-2 hover:bg-gray-100 rounded-xl transition-colors">
                        <ArrowLeft className="text-gray-600" />
                    </button>

                    <div className="flex gap-3">
                        <div className="flex items-center gap-2 bg-yellow-400/20 px-3 py-1.5 rounded-full border-2 border-yellow-400">
                            <Star className="text-yellow-500 fill-yellow-500" size={18} />
                            <span className="font-black text-yellow-700">1,250</span>
                        </div>
                        <button
                            onClick={() => navigate('/parent/stats')}
                            className="flex items-center gap-2 bg-indigo-500 px-3 py-1.5 rounded-full border-2 border-white shadow-md hover:bg-indigo-600 transition-all group"
                        >
                            <Shield size={18} className="text-white group-hover:rotate-12 transition-transform" />
                            <span className="text-white font-bold text-sm">Родителям</span>
                        </button>
                    </div>

                    <div className="w-10 h-10 rounded-full border-2 border-white shadow-md overflow-hidden bg-indigo-200">
                        <img src="https://api.dicebear.com/7.x/adventurer/svg?seed=Nazarbek" alt="avatar" />
                    </div>
                </div>
            </div>

            {/* Основная карта */}
            <div className="relative max-w-lg mx-auto mt-10 px-4">

                {/* Динамическая линия пути */}
                <svg
                    className="absolute top-0 left-0 w-full h-full pointer-events-none z-0"
                    style={{ minHeight: `${lessons.length * 200}px` }}
                >
                    <path
                        d={lessons.map((_, i) => {
                            const y = i * 220 + 100;
                            const nextY = (i + 1) * 220 + 100;
                            const x = i % 2 === 0 ? 100 : 300;
                            const nextX = (i + 1) % 2 === 0 ? 100 : 300;
                            if (i === lessons.length - 1) return "";
                            return `M ${x} ${y} C ${x} ${y + 110}, ${nextX} ${y + 110}, ${nextX} ${nextY}`;
                        }).join(" ")}
                        fill="none"
                        stroke="#6366f1"
                        strokeWidth="12"
                        strokeDasharray="20"
                        strokeLinecap="round"
                        className="opacity-20"
                    />
                </svg>

                {/* Уровни */}
                <div className="flex flex-col relative z-10">
                    {lessons.length > 0 ? (
                        lessons.map((lesson, index) => {
                            const isEven = index % 2 === 0;
                            return (
                                <div
                                    key={lesson.id}
                                    className={`flex w-full mb-32 ${isEven ? 'justify-start ml-4' : 'justify-end mr-4'}`}
                                >
                                    <motion.div
                                        initial={{ scale: 0.5, opacity: 0 }}
                                        whileInView={{ scale: 1, opacity: 1 }}
                                        viewport={{ once: true, margin: "-50px" }}
                                        className="flex flex-col items-center"
                                    >
                                        {/* Облачко с названием */}
                                        <div className="mb-4 bg-white px-5 py-2 rounded-2xl shadow-lg border-b-4 border-gray-100 font-black text-slate-700 text-sm whitespace-nowrap">
                                            {lesson.title}
                                        </div>

                                        {/* Кнопка-остров */}
                                        <motion.button
                                            whileHover={lesson.status !== 'locked' ? { scale: 1.1, y: -5 } : {}}
                                            whileTap={{ scale: 0.9 }}
                                            onClick={() => handleStart(lesson)}
                                            className={`
                                                relative w-28 h-28 rounded-[40px] flex items-center justify-center transition-all
                                                ${lesson.status === 'done' ? 'bg-green-400 border-b-[12px] border-green-600 shadow-[0_20px_50px_rgba(74,222,128,0.3)]' :
                                                lesson.status === 'active' ? 'bg-yellow-400 border-b-[12px] border-yellow-600 shadow-[0_20px_50px_rgba(250,204,21,0.4)] animate-pulse' :
                                                    'bg-gray-300 border-b-[12px] border-gray-400 opacity-80 cursor-not-allowed'}
                                            `}
                                        >
                                            {lesson.status === 'locked' ? (
                                                <Lock size={42} className="text-gray-500" />
                                            ) : (
                                                <div className="bg-white p-3 rounded-2xl shadow-inner">
                                                    <Play size={32} className="text-indigo-600 fill-indigo-600 ml-1" />
                                                </div>
                                            )}

                                            {/* XP Награда */}
                                            <div className="absolute -bottom-8 bg-indigo-600 text-white px-3 py-1 rounded-full text-[12px] font-black shadow-lg border-2 border-white">
                                                {lesson.xpReward} XP
                                            </div>
                                        </motion.button>
                                    </motion.div>
                                </div>
                            );
                        })
                    ) : (
                        <div className="text-center py-20 font-bold text-slate-500">
                            Ой! Уроки потерялись по дороге... 🦖
                        </div>
                    )}
                </div>
            </div>

            {/* Футер */}
            <div className="mt-20 pb-10 text-center">
                <div className="inline-block bg-white/50 backdrop-blur-sm px-6 py-2 rounded-full font-bold text-slate-500 text-sm">
                    🌈 Конец первой главы. Продолжение следует!
                </div>
            </div>
        </div>
    );
};

export default ChildMap;