
import { Outlet } from "react-router-dom";

import Sidebar from "./Sidebar";
import Topbar from "./Topbar";

export default function MainLayout() {
  return (
    <div className="app-layout">

      {/* Left Sidebar */}
      <Sidebar />

      {/* Main Content Area */}
      <div className="main-area">

        {/* Top Navigation Bar */}
        <Topbar />

        {/* Page Content */}
        <main className="page-content">
          <Outlet />
        </main>

      </div>

    </div>
  );
}
