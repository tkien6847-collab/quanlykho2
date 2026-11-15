import type { Role } from "../types";

interface HeaderProps {
  role: Role;
  username: string;
  onLogout: () => void;
  onMenuClick: () => void;
}

const roleLabels: Record<Role, string> = {
  admin: "Quản trị viên",
  manager: "Quản lý",
  employee: "Nhân viên",
};

export default function Header({ role, username, onLogout, onMenuClick }: HeaderProps) {
  return (
    <header className="topbar">
      <button className="mobile-menu icon-button" aria-label="Mở menu" onClick={onMenuClick}>
        ☰
      </button>
      <div className="topbar-context">
        <span className="topbar-context-label">KHÔNG GIAN LÀM VIỆC</span>
        <strong>Quản lý kho</strong>
      </div>
      <div className="topbar-actions">
        <div className="account-identity">
          <span className="avatar" aria-hidden="true">{username.slice(0, 2).toLocaleUpperCase("vi")}</span>
          <span className="account-copy"><strong>{username}</strong><small>{roleLabels[role]}</small></span>
        </div>
        <button className="logout-button" onClick={onLogout}>Đăng xuất</button>
      </div>
    </header>
  );
}
