import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import { ArrowLeft, Star, Volume2, Loader2 } from 'lucide-react';
import confetti from 'canvas-confetti';
import api from '../api/axios';

const Exercise = () => {
    const { id } = useParams();
    const navigate = useNavigate();

    const [questions, setQuestions] = useState([]);
    const [step, setStep] = useState(0);
    const [score, setScore] = useState(0);
    const [loading, setLoading] = useState(true);

    // Загрузка заданий
    useEffect(() => {
        const fetchExercises = async () => {
            try {
                const response = await api.get(`/exercises/lesson/${id}`);
                setQuestions(response.data);
            } catch (error) {
                console.error("Ошибка при загрузке заданий:", error);
            } finally {
                setLoading(false);
            }
        };
        fetchExercises();
    }, [id]);

    const currentQuestion = questions[step];


    const handleAnswer = async (option) => {
        const isCorrect = option === currentQuestion.correctAnswer;

        // 1. Отправляем данные в БД сразу при клике
        try {
            await api.post('/results', {
                childId: 1,
                exerciseId: currentQuestion.id,
                correct: isCorrect,
                timeTaken: 10
            });
            console.log("Результат успешно сохранен!");
        } catch (error) {
            console.error("Ошибка сохранения результата:", error);
        }

        // (звуки, анимации)
        if (isCorrect) {
            confetti({
                particleCount: 150,
                spread: 70,
                origin: { y: 0.6 },
                colors: ['#4ade80', '#fbbf24', '#38bdf8']
            });

            const synth = window.speechSynthesis;
            const utter = new SpeechSynthesisUtterance("Правильно!");
            utter.lang = 'ru-RU';
            synth.speak(utter);

            if (step < questions.length - 1) {
                setStep(step + 1);
                setScore(prev => prev + 10);
            } else {
                const finalUtter = new SpeechSynthesisUtterance("Уровень пройден! Ты супер!");
                finalUtter.lang = 'ru-RU';
                synth.speak(finalUtter);
                alert(`Уровень пройден! +${score + 10} XP 🏆`);
                navigate('/child');
            }
        } else {
            const synth = window.speechSynthesis;
            const utter = new SpeechSynthesisUtterance("Попробуй еще раз");
            utter.lang = 'ru-RU';
            synth.speak(utter);
            alert("Ой! Попробуй еще раз 🦖");
        }
    };

    if (loading) return (
        <div className="h-screen flex flex-col items-center justify-center bg-indigo-50">
            <Loader2 className="animate-spin text-indigo-600" size={48} />
            <p className="mt-4 font-bold text-indigo-900">Готовим задание...</p>
        </div>
    );

    if (questions.length === 0) return (
        <div className="h-screen flex flex-col items-center justify-center bg-indigo-50 p-6 text-center">
            <h1 className="text-2xl font-bold text-slate-800">В этом уроке пока нет заданий 🦖</h1>
            <button onClick={() => navigate('/child')} className="mt-6 bg-indigo-600 text-white px-8 py-3 rounded-full font-bold">
                Вернуться к карте
            </button>
        </div>
    );

    return (
        <div className="min-h-screen bg-indigo-50 p-4 sm:p-6 flex flex-col items-center font-sans">
            {/* Header */}
            <div className="w-full max-w-xl flex justify-between items-center mb-8 bg-white p-4 rounded-3xl shadow-sm border-b-4 border-indigo-100">
                <button onClick={() => navigate('/child')} className="p-2 hover:bg-indigo-50 rounded-2xl transition-colors">
                    <ArrowLeft size={30} className="text-indigo-600" />
                </button>
                <div className="flex items-center gap-2 bg-yellow-400 px-4 py-2 rounded-2xl text-white font-black shadow-sm">
                    <Star fill="white" size={20} /> {score}
                </div>
            </div>

            {/* Progress Bar */}
            <div className="w-full max-w-xl bg-white h-5 rounded-full mb-10 p-1 shadow-inner border border-indigo-100">
                <motion.div
                    className="bg-green-400 h-full rounded-full"
                    initial={{ width: 0 }}
                    animate={{ width: `${((step + 1) / questions.length) * 100}%` }}
                />
            </div>

            {/* Question Card */}
            <AnimatePresence mode="wait">
                <motion.div
                    key={step}
                    initial={{ y: 20, opacity: 0 }}
                    animate={{ y: 0, opacity: 1 }}
                    exit={{ y: -20, opacity: 0 }}
                    className="bg-white w-full max-w-xl rounded-[40px] p-8 sm:p-12 shadow-2xl border-b-[12px] border-indigo-100 text-center relative overflow-hidden"
                >
                    <div className="flex justify-center mb-8">
                        <button
                            onClick={() => {
                                const ut = new SpeechSynthesisUtterance(currentQuestion.question);
                                ut.lang = 'ru-RU';
                                window.speechSynthesis.speak(ut);
                            }}
                            className="p-5 bg-indigo-50 rounded-full text-indigo-600 hover:scale-110 active:scale-95 transition-all shadow-sm"
                        >
                            <Volume2 size={40} />
                        </button>
                    </div>

                    <h1 className="text-3xl sm:text-4xl font-black text-slate-800 mb-12 leading-tight">
                        {currentQuestion.question}
                    </h1>

                    <div className="grid grid-cols-1 gap-4">
                        {currentQuestion.options && (() => {
                            const optionsArray = typeof currentQuestion.options === 'string'
                                ? JSON.parse(currentQuestion.options)
                                : currentQuestion.options;

                            return optionsArray.map((opt, idx) => (
                                <motion.button
                                    key={idx}
                                    whileHover={{ scale: 1.02 }}
                                    whileTap={{ scale: 0.98 }}
                                    onClick={() => handleAnswer(opt)}
                                    className="py-6 bg-white border-2 border-indigo-100 hover:border-indigo-400 hover:bg-indigo-50 text-3xl font-black text-indigo-600 rounded-3xl transition-all shadow-md active:border-b-0 active:translate-y-1"
                                >
                                    {opt}
                                </motion.button>
                            ));
                        })()}
                    </div>
                </motion.div>
            </AnimatePresence>
        </div>
    );
};

export default Exercise;