import { useCallback, useEffect, useState } from "react";
import Footer from "./layout/Footer";
import Header from "./layout/Header";
import Sidebar, { getNavigation } from "./layout/Sidebar";
import LoginPage from "./components/LoginPage";
import Workspace from "./components/Workspace";
import { apiRequest } from "./services/api";
import type { AuthUser, LoginResponse, PageId, Role } from "./types";

function roleFromName(name: string): Role | null {
  const normalized = name.normalize("NFD").replace(/[\u0300-\u036f]/g, "").toLocaleLowerCase("vi");
  if (normalized.includes("admin") || normalized.includes("quan tri")) return "admin";
  if (normalized.includes("manager") || normalized.includes("quan ly")) return "manager";
  if (normalized.includes("employee") || normalized.includes("nhan vien")) return "employee";
  return null;
}

export default function App() {
  const [user, setUser] = useState<AuthUser | null>(null);
  const [authReady, setAuthReady] = useState(false);
  const [authError, setAuthError] = useState("");
  const [page, setPage] = useState<PageId>("dashboard");
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const role = user ? roleFromName(user.tenVaiTro) : null;

  useEffect(() => {
    const token = sessionStorage.getItem("accessToken");
    if (!token) {
      setAuthReady(true);
      return;
    }
    void apiRequest<AuthUser>("/api/auth/me")
      .then((identity) => {
        if (!roleFromName(identity.tenVaiTro)) {
          throw new Error(`Vai trò "${identity.tenVaiTro}" chưa được cấu hình cho giao diện.`);
        }
        setUser(identity);
      })
      .catch((cause: unknown) => {
        sessionStorage.removeItem("accessToken");
        setAuthError(cause instanceof Error ? cause.message : "Phiên đăng nhập không còn hợp lệ.");
      })
      .finally(() => setAuthReady(true));
  }, []);

  const login = useCallback((session: LoginResponse) => {
    if (!roleFromName(session.tenVaiTro)) {
      setAuthError(`Vai trò "${session.tenVaiTro}" chưa được cấu hình cho giao diện.`);
      return;
    }
    sessionStorage.setItem("accessToken", session.accessToken);
    setAuthError("");
    setPage("dashboard");
    setUser({
      tenDangNhap: session.tenDangNhap,
      maVaiTro: session.maVaiTro,
      tenVaiTro: session.tenVaiTro,
      maNV: session.maNV,
    });
  }, []);

  const logout = () => {
    sessionStorage.removeItem("accessToken");
    setUser(null);
    setPage("dashboard");
    setSidebarOpen(false);
  };

  if (!authReady) {
    return <div className="auth-loading"><span className="loading-spinner" />Đang kiểm tra phiên đăng nhập...</div>;
  }

  if (!user || !role) {
    return <LoginPage key={authError} initialMessage={authError} onLogin={login} />;
  }

  const allowedPages = getNavigation(role).map((item) => item.id);
  const navigate = (nextPage: PageId) => {
    if (nextPage === "profile" || allowedPages.includes(nextPage)) setPage(nextPage);
  };

  return (
    <div className="app-shell">
      <Sidebar
        role={role}
        activePage={page}
        isOpen={sidebarOpen}
        onNavigate={navigate}
        onClose={() => setSidebarOpen(false)}
      />
      <div className="main-shell">
        <Header
          role={role}
          username={user.tenDangNhap}
          onProfileClick={() => navigate("profile")}
          onLogout={logout}
          onMenuClick={() => setSidebarOpen(true)}
        />
        <main className="main-content">
          {authError && <div className="alert alert-error session-alert" role="alert">{authError}</div>}
          <Workspace page={page} role={role} employeeId={user.maNV} onNavigate={navigate} />
          <Footer />
        </main>
      </div>
    </div>
  );
}
