/** @type {import('tailwindcss').Config} */
export default {
    content: [
        "./index.html",
        "./src/**/*.{js,ts,jsx,tsx}",
    ],
    theme: {
        extend: {
            fontFamily: {
                kids: ['Nunito', 'sans-serif'],
            },
            colors: {
                kidPrimary: '#FF6B6B',
                kidSecondary: '#4ECDC4',
                kidYellow: '#FFE66D',
            },
        },
    },
    plugins: [],
}