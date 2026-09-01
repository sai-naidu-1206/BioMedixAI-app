/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/pages/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/components/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/app/**/*.{js,ts,jsx,tsx,mdx}",
  ],
  theme: {
    extend: {
      colors: {
        background: "#F3F0FF", // Clean Off-White with soft lavender undertones
        charcoal: "#1E1B4B", // Dark Charcoal for high contrast readability
        clay: {
          violet: "#8B5CF6",
          periwinkle: "#A78BFA",
          lavender: "#C4B5FD",
          mint: "#34D399",
          cyan: "#06B6D4",
          amber: "#F59E0B",
          pink: "#EC4899",
          card: "#FFFFFF",
          surface: "#FAF8FF",
        }
      },
      borderRadius: {
        '3xl': '1.75rem',
        '4xl': '2.25rem',
      },
      boxShadow: {
        'clay-sm': '4px 4px 10px #D8D2ED, -4px -4px 10px #FFFFFF',
        'clay-md': '8px 8px 18px #D1CAEA, -8px -8px 18px #FFFFFF',
        'clay-lg': '14px 14px 28px #CAC1E7, -14px -14px 28px #FFFFFF',
        'clay-inset': 'inset 4px 4px 8px #D8D2ED, inset -4px -4px 8px #FFFFFF',
        'clay-glow-violet': '0 12px 28px -6px rgba(139, 92, 246, 0.45)',
        'clay-glow-mint': '0 12px 28px -6px rgba(52, 211, 153, 0.45)',
      },
    },
  },
  plugins: [],
}
