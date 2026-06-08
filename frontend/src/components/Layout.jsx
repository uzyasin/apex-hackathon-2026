import { NavLink } from 'react-router-dom';

const NAV = [
  { to: '/planning', label: 'Akıllı Planlama' },
  { to: '/decompose', label: 'Görev Kırılımı' },
  { to: '/dashboard', label: 'Sprint Dashboard' },
];

export default function Layout({ children }) {
  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-white border-b border-gray-200 px-6 py-0 flex items-center gap-8">
        <div className="py-4">
          <span className="text-lg font-bold text-gray-900">Agile</span>
          <span className="text-lg font-bold text-blue-600">AI</span>
          <span className="ml-2 text-xs text-gray-400 font-normal">by IFTS Hackathon</span>
        </div>
        <nav className="flex gap-1">
          {NAV.map(({ to, label }) => (
            <NavLink
              key={to}
              to={to}
              className={({ isActive }) =>
                `px-4 py-4 text-sm font-medium border-b-2 transition-colors ${
                  isActive
                    ? 'border-blue-600 text-blue-600'
                    : 'border-transparent text-gray-500 hover:text-gray-800'
                }`
              }
            >
              {label}
            </NavLink>
          ))}
        </nav>
      </header>
      <main className="max-w-5xl mx-auto px-6 py-8">{children}</main>
    </div>
  );
}
