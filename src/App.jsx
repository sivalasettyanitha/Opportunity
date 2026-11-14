
import { Routes, Route } from "react-router-dom";

import MainLayout from "./components/layout/MainLayout";
import ProtectedRoute from "./components/common/ProtectedRoute";
import PublicRoute from "./components/common/PublicRoute";

import Dashboard from "./pages/dashboard/Dashboard";
import Calendar from "./pages/calendar/Calendar";
import Events from "./pages/events/Events";
import Reminders from "./pages/reminders/Reminders";
import Tasks from "./pages/tasks/Tasks";
import Opportunities from "./pages/opportunities/Opportunities";
import Statistics from "./pages/statistics/Statistics";
import Profile from "./pages/profile/Profile";
import Settings from "./pages/settings/Settings";

import Login from "./pages/auth/Login";
import Register from "./pages/auth/Register";

function App() {
  return (
    <Routes>

      {/* Public authentication pages */}
      <Route element={<PublicRoute />}>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
      </Route>

      {/* Protected application pages */}
      <Route element={<ProtectedRoute />}>
        <Route element={<MainLayout />}>
          <Route path="/" element={<Dashboard />} />
          <Route path="/calendar" element={<Calendar />} />
          <Route path="/events" element={<Events />} />
          <Route path="/reminders" element={<Reminders />} />
          <Route path="/tasks" element={<Tasks />} />
          <Route path="/opportunities" element={<Opportunities />} />
          <Route path="/statistics" element={<Statistics />} />
          <Route path="/profile" element={<Profile />} />
          <Route path="/settings" element={<Settings />} />
        </Route>
      </Route>

    </Routes>
  );
}

export default App;
