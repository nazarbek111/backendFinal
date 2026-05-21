import React, { useEffect, useState } from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import { BookOpen, Clock, Award, TrendingUp, ChevronRight, ArrowLeft, Loader2 } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios'; // Убедись, что путь к axios верный

const ParentDashboard = () => {
    const navigate = useNavigate();
    const [results, setResults] = useState([]);
    const [loading, setLoading] = useState(true);

    const childId = 1; // Твой тестовый ID

    useEffect(() => {
        const fetchStats = async () => {
            try {
                const response = await api.get(`/results/child/${childId}`);

                const data = response.data.content ? response.data.content : response.data;

                setResults(Array.isArray(data) ? data : []);

            } catch (err) {
                console.error("Ошибка загрузки статистики:", err);
                setResults([]); // В случае ошибки ставим пустой массив
            } finally {
                setLoading(false);
            }
        };
        fetchStats();
    }, []);

    // РАСЧЕТ РЕАЛЬНЫХ ЦИФР
    const totalAnswers = Array.isArray(results) ? results.length : 0;
    const correctAnswers = Array.isArray(results) ? results.filter(r => r.correct).length : 0;
    const accuracy = totalAnswers > 0 ? Math.round((correctAnswers / totalAnswers) * 100) : 0;
    const uniqueLessons = [...new Set(results.map(r => r.id))].length;

    // Формируем данные для графика (группируем по дням недели)
    // Для простоты примера оставим статику или можно сделать группировку по submittedAt
    const chartData = [
        { day: 'Пн', xp: 100 }, { day: 'Вт', xp: 200 }, { day: 'Ср', xp: 150 },
        { day: 'Чт', xp: 300 }, { day: 'Пт', xp: accuracy * 5 }, // Привязали к точности для теста
        { day: 'Сб', xp: results.length * 10 },
        { day: 'Вс', xp: 400 },
    ];

    if (loading) return (
        <div className="h-screen flex items-center justify-center bg-slate-50">
            <Loader2 className="animate-spin text-indigo-600" size={48} />
        </div>
    );

    return (
        <div className="min-h-screen bg-slate-50 p-4 md:p-8 font-sans">
            <div className="max-w-6xl mx-auto">
                {/* КНОПКА ВОЗВРАТА */}
                <button
                    onClick={() => navigate('/child')}
                    className="mb-6 flex items-center gap-2 text-indigo-600 font-bold hover:bg-indigo-50 px-4 py-2 rounded-xl transition-colors"
                >
                    <ArrowLeft size={20} /> Вернуться к карте
                </button>

                {/* Header */}
                <div className="flex justify-between items-center mb-10">
                    <div>
                        <h1 className="text-3xl font-black text-slate-800">Кабинет родителей 🛡️</h1>
                        <p className="text-slate-500 font-medium">Контролируйте прогресс Назарбека</p>
                    </div>
                </div>

                {/* Quick Stats - ТЕПЕРЬ РЕАЛЬНЫЕ */}
                <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-10">
                    <StatCard icon={<Clock className="text-blue-500"/>} title="Всего попыток" value={totalAnswers} sub="ответы" color="bg-blue-50"/>
                    <StatCard icon={<BookOpen className="text-purple-500"/>} title="Уникальных задач" value={uniqueLessons} sub="пройдено" color="bg-purple-50"/>
                    <StatCard icon={<Award className="text-orange-500"/>} title="Точность ответов" value={`${accuracy}%`} sub={accuracy > 70 ? "Отлично!" : "Нужно подтянуть"} color="bg-orange-50"/>
                </div>

                <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
                    {/* Activity Chart */}
                    <div className="bg-white p-6 rounded-[32px] shadow-sm border border-slate-100">
                        <h3 className="text-xl font-black text-slate-800 mb-6 flex items-center gap-2">
                            <TrendingUp size={24} className="text-green-500"/> Активность (XP)
                        </h3>
                        <div className="h-[300px] w-full">
                            <ResponsiveContainer width="100%" height="100%">
                                <LineChart data={chartData}>
                                    <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#f1f5f9"/>
                                    <XAxis dataKey="day" axisLine={false} tickLine={false} tick={{fill: '#64748b', fontWeight: 'bold'}}/>
                                    <YAxis hide/>
                                    <Tooltip />
                                    <Line type="monotone" dataKey="xp" stroke="#6366f1" strokeWidth={4} dot={{r: 6, fill: '#6366f1'}} />
                                </LineChart>
                            </ResponsiveContainer>
                        </div>
                    </div>

                    {/* Skill Breakdown - Можно привязать к категориям упражнений */}
                    <div className="bg-white p-6 rounded-[32px] shadow-sm border border-slate-100">
                        <h3 className="text-xl font-black text-slate-800 mb-6">Освоение навыков</h3>
                        <div className="space-y-6">
                            <SkillProgress label="Общий прогресс" percent={accuracy} color="bg-green-400" />
                            <SkillProgress label="Внимательность" percent={totalAnswers > 0 ? 80 : 0} color="bg-blue-400" />
                            <SkillProgress label="Скорость" percent={Math.max(0, 100 - (totalAnswers * 2))} color="bg-orange-400" />
                        </div>
                    </div>
                </div>

                {/* Recent Activity List - РЕАЛЬНЫЕ ДАННЫЕ ИЗ БАЗЫ */}
                <div className="mt-8 bg-white p-6 rounded-[32px] shadow-sm border border-slate-100">
                    <h3 className="text-xl font-black text-slate-800 mb-6">Последние ответы в уроках</h3>
                    <div className="space-y-4">
                        {results.slice(0, 5).map((res) => (
                            <ActivityRow
                                key={res.id}
                                icon={res.correct ? "✅" : "❌"}
                                title={res.exerciseQuestion}
                                time={new Date(res.submittedAt).toLocaleTimeString()}
                                xp={res.correct ? "+10 XP" : "0 XP"}
                            />
                        ))}
                        {results.length === 0 && <p className="text-center text-slate-400 py-4">Данных пока нет. Начните заниматься! 🦖</p>}
                    </div>
                </div>
            </div>
        </div>
    );
};

// Вспомогательные компоненты (оставляем без изменений)
const StatCard = ({ icon, title, value, sub, color }) => (
    <div className={`${color} p-6 rounded-[32px] border-b-4 border-slate-200/50`}>
        <div className="bg-white w-12 h-12 rounded-2xl flex items-center justify-center mb-4 shadow-sm">{icon}</div>
        <p className="text-slate-600 font-bold text-sm">{title}</p>
        <div className="flex items-baseline gap-2">
            <h2 className="text-3xl font-black text-slate-800">{value}</h2>
            <span className="text-xs font-bold text-slate-400 uppercase">{sub}</span>
        </div>
    </div>
);

const SkillProgress = ({ label, percent, color }) => (
    <div>
        <div className="flex justify-between mb-2">
            <span className="font-bold text-slate-700">{label}</span>
            <span className="font-black text-slate-900">{percent}%</span>
        </div>
        <div className="w-full bg-slate-100 h-3 rounded-full overflow-hidden">
            <div className={`${color} h-full rounded-full transition-all duration-1000`} style={{ width: `${percent}%` }} />
        </div>
    </div>
);

const ActivityRow = ({ icon, title, time, xp }) => (
    <div className="flex items-center justify-between p-4 hover:bg-slate-50 rounded-2xl transition-colors group">
        <div className="flex items-center gap-4">
            <span className="text-2xl">{icon}</span>
            <div>
                <p className="font-bold text-slate-800">{title}</p>
                <p className="text-xs text-slate-400 font-medium">{time}</p>
            </div>
        </div>
        <div className="flex items-center gap-3">
            <span className="font-black text-indigo-600 text-sm">{xp}</span>
            <ChevronRight size={20} className="text-slate-300 group-hover:text-slate-500"/>
        </div>
    </div>
);

export default ParentDashboard;