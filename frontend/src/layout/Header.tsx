import { useEffect, useId, useRef, useState } from "react";
import type { Role } from "../types";

interface HeaderProps {
  role: Role;
  username: string;
  onProfileClick: () => void;
  onLogout: () => void;
  onMenuClick: () => void;
}

const roleLabels: Record<Role, string> = {
  admin: "Quản trị viên",
  manager: "Quản lý",
  employee: "Nhân viên",
};

export default function Header({ role, username, onProfileClick, onLogout, onMenuClick }: HeaderProps) {
  const [accountMenuOpen, setAccountMenuOpen] = useState(false);
  const accountMenuId = useId();
  const accountMenuRef = useRef<HTMLDivElement>(null);
  const accountButtonRef = useRef<HTMLButtonElement>(null);

  useEffect(() => {
    if (!accountMenuOpen) return;

    const closeOnOutside = (event: Event) => {
      if (event.target instanceof Node && !accountMenuRef.current?.contains(event.target)) {
        setAccountMenuOpen(false);
      }
    };
    const closeOnEscape = (event: KeyboardEvent) => {
      if (event.key === "Escape") {
        setAccountMenuOpen(false);
        accountButtonRef.current?.focus();
      }
    };

    document.addEventListener("pointerdown", closeOnOutside);
    document.addEventListener("focusin", closeOnOutside);
    document.addEventListener("keydown", closeOnEscape);
    return () => {
      document.removeEventListener("pointerdown", closeOnOutside);
      document.removeEventListener("focusin", closeOnOutside);
      document.removeEventListener("keydown", closeOnEscape);
    };
  }, [accountMenuOpen]);

  const showProfile = () => {
    setAccountMenuOpen(false);
    accountButtonRef.current?.focus();
    onProfileClick();
  };

  return (
    <header className="topbar">
      <button className="mobile-menu icon-button" aria-label="Mở menu" onClick={onMenuClick}>
        ☰
      </button>
      <div className="topbar-context">
        <span className="topbar-context-label">KHÔNG GIAN LÀM VIỆC</span>
        <strong>Quản lý kho</strong>
      </div>
      <div className="topbar-actions account-menu" ref={accountMenuRef}>
        <button
          type="button"
          className="account-menu-trigger"
          ref={accountButtonRef}
          aria-label={`Menu tài khoản ${username}`}
          aria-expanded={accountMenuOpen}
          aria-controls={accountMenuId}
          onClick={() => setAccountMenuOpen((open) => !open)}
        >
          <span className="avatar" aria-hidden="true">{username.slice(0, 2).toLocaleUpperCase("vi")}</span>
          <span className="account-menu-name">{username}</span>
          <span className="account-menu-caret" aria-hidden="true" />
        </button>
        <div className="account-menu-popover" id={accountMenuId} hidden={!accountMenuOpen}>
          <div className="account-menu-summary">
            <strong>{username}</strong>
            <span>{roleLabels[role]}</span>
          </div>
          <button type="button" className="account-menu-item" onClick={showProfile}>
            <span aria-hidden="true">♙</span>
            Thông tin cá nhân
          </button>
          <button type="button" className="account-menu-item account-menu-logout" onClick={onLogout}>
            <span aria-hidden="true">↪</span>
            Đăng xuất
          </button>
        </div>
      </div>
    </header>
  );
}
