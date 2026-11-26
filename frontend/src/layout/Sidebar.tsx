import type { NavigationItem, PageId, Role } from "../types";

interface SidebarProps {
  role: Role;
  activePage: PageId;
  isOpen: boolean;
  onNavigate: (page: PageId) => void;
  onClose: () => void;
}

const navigationByRole: Record<Role, NavigationItem[]> = {
  admin: [
    { id: "dashboard", label: "Tổng quan", icon: "▦", group: "TỔNG QUAN" },
    { id: "accounts", label: "Quản lý tài khoản", icon: "♙", group: "QUẢN TRỊ" },
    { id: "categories", label: "Quản lý danh mục", icon: "▤", group: "QUẢN TRỊ" },
    { id: "reports", label: "Thống kê & báo cáo", icon: "▤", group: "QUẢN TRỊ" },
    { id: "manage-inout", label: "Quản lý nhập xuất", icon: "⇅", group: "QUẢN TRỊ" },
  ],
  manager: [
    { id: "dashboard", label: "Tổng quan", icon: "▦", group: "TỔNG QUAN" },
    { id: "reports", label: "Thống kê & báo cáo", icon: "▤", group: "KHO HÀNG" },
    { id: "partners", label: "NCC / Khách hàng", icon: "♧", group: "KHO HÀNG" },
    { id: "employees", label: "Quản lý nhân viên", icon: "♙", group: "KHO HÀNG" },
    { id: "manage-inout", label: "Quản lý nhập xuất", icon: "⇅", group: "KHO HÀNG" },
    { id: "inventory", label: "Quản lý hàng hóa", icon: "▣", group: "KHO HÀNG" },
    { id: "storage", label: "Vị trí lưu trữ", icon: "▤", group: "KHO HÀNG" },
    { id: "categories", label: "Quản lý danh mục", icon: "▤", group: "KHO HÀNG" },
  ],
  employee: [
    { id: "dashboard", label: "Tổng quan", icon: "▦", group: "TỔNG QUAN" },
    { id: "outbound", label: "Lập phiếu xuất kho", icon: "↑", group: "NGHIỆP VỤ" },
    { id: "inbound", label: "Lập phiếu nhập kho", icon: "↓", group: "NGHIỆP VỤ" },
    { id: "inventory", label: "Quản lý hàng hóa", icon: "▣", group: "NGHIỆP VỤ" },
  ],
};

export function getNavigation(role: Role) {
  return navigationByRole[role];
}

export default function Sidebar({ role, activePage, isOpen, onNavigate, onClose }: SidebarProps) {
  const items = navigationByRole[role];
  const groups = [...new Set(items.map((item) => item.group))];

  return (
    <>
      {isOpen && <button className="sidebar-backdrop" aria-label="Đóng menu" onClick={onClose} />}
      <aside className={`sidebar ${isOpen ? "sidebar-open" : ""}`}>
        <a className="brand" href="#" onClick={(event) => { event.preventDefault(); onNavigate("dashboard"); }}>
          <span className="brand-mark">K</span>
          <span className="brand-copy">
            <strong>KhoFlow</strong>
            <small>WAREHOUSE SYSTEM</small>
          </span>
        </a>
        <div className="workspace-badge">
          <span className="workspace-dot" />
          <span>Kho hàng</span>
          <span className="workspace-chevron">⌄</span>
        </div>
        <nav className="sidebar-nav" aria-label="Điều hướng chính">
          {groups.map((group) => (
            <div className="nav-group" key={group}>
              <p className="nav-heading">{group}</p>
              {items.filter((item) => item.group === group).map((item) => (
                <button
                  className={`nav-item ${activePage === item.id || (item.id === "inventory" && activePage === "stock-search") ? "nav-item-active" : ""}`}
                  key={item.id}
                  onClick={() => { onNavigate(item.id); onClose(); }}
                >
                  <span className="nav-icon">{item.icon}</span>
                  <span>{item.label}</span>
                  {(activePage === item.id || (item.id === "inventory" && activePage === "stock-search")) && <span className="nav-active-mark" />}
                </button>
              ))}
            </div>
          ))}
        </nav>
        <div className="sidebar-bottom">
          <div className="support-card">
            <span className="support-icon">?</span>
            <strong>Cần hỗ trợ?</strong>
            <span>Liên hệ quản trị hệ thống</span>
          </div>
          <div className="sidebar-version">KhoFlow <span>Phiên bản giao diện</span></div>
        </div>
      </aside>
    </>
  );
}
