export type Role = "admin" | "manager" | "employee";

export interface AuthUser {
  tenDangNhap: string;
  maVaiTro: number;
  tenVaiTro: string;
  maNV: number;
}

export interface LoginResponse extends AuthUser {
  accessToken: string;
}

export type PageId =
  | "dashboard"
  | "profile"
  | "accounts"
  | "categories"
  | "employees"
  | "inventory"
  | "stock-search"
  | "partners"
  | "reports"
  | "inbound"
  | "outbound"
  | "approve-inbound"
  | "approve-outbound"
  | "manage-inout"
  | "storage";

export interface NavigationItem {
  id: PageId;
  label: string;
  icon: string;
  group: string;
}

export interface Account {
  maTK: number;
  tenDangNhap: string;
  trangThai: string;
  maVaiTro: number;
  maNV: number;
  hoTen: string;
  tenVaiTro: string;
}

export interface PersonalProfile {
  hoTen: string;
  gioiTinh: string | null;
  ngaySinh: string | null;
  soDienThoai: string | null;
  email: string | null;
  diaChi: string | null;
}

export interface Product {
  maHH?: number;
  tenHH: string;
  donViTinh: string;
  donGia: number;
  soLuongTon: number;
  viTriKe?: string;
  hinhAnh?: string;
  trangThai: string;
  tenDM: string;
  maDM?: number;
}

export interface Category {
  maDM: number;
  tenDM: string;
  moTa: string | null;
  soMatHang: number;
  trangThai: ActivityStatus;
}

export type CategoryOption = Pick<Category, "maDM" | "tenDM">;

export type ActivityStatus = string;

export interface Supplier {
  maNCC: number;
  maNccCode?: string;
  tenNCC: string;
  soDienThoai: string;
  email: string;
  diaChi: string;
  trangThai: ActivityStatus;
}

export interface Customer {
  maKH: number;
  hoTen: string;
  soDienThoai: string;
  email: string;
  diaChi: string;
  trangThai: ActivityStatus;
}

export interface Employee {
  maNV: number;
  hoTen: string;
  gioiTinh: string | null;
  ngaySinh: string | null;
  soDienThoai: string | null;
  email: string | null;
  diaChi: string | null;
  chucVu: string;
  tenDangNhap: string | null;
  trangThai: ActivityStatus | null;
}

export interface InventoryDocument {
  maPN?: number;
  maPX?: number;
  ngayNhap?: string;
  ngayXuat?: string;
  tongTien: number;
  ghiChu?: string;
  maNV: number;
  maNCC?: number;
  maKH?: number;
  trangThai: string;
}

export interface DocumentLine {
  maHH: number;
  soLuong: number;
  donGia: number;
  hanSuDung?: string;
}

export interface InboundDocumentDetail {
  chiTiet: {
    id: {
      maPN: number;
      maHH: number;
    };
    soLuongNhap: number;
    donGiaNhap: number;
    hanSuDung: string | null;
  }[];
}

export interface WarehouseReport {
  ngay: string;
  thang: string;
  soLuongNhapTrongNgay: number;
  soLuongXuatTrongNgay: number;
  soLuongNhapTrongThang: number;
  soLuongXuatTrongThang: number;
  soPhieuChoDuyet: number;
}

export interface ReportFilterResult {
  tuNgay: string;
  denNgay: string;
  soLuongNhap: number;
  soLuongXuat: number;
  theoDanhMuc: { maDM: number; tenDM: string; soLuongNhap: number; soLuongXuat: number }[];
}

export interface ReportChart {
  cheDo: string;
  moc: { ngay: string; nhan: string; soLuongNhap: number; soLuongXuat: number; tonKhoLuyKe: number }[];
}

export interface StorageSlot {
  maViTri: number;
  maViTriCode: string;
  khuVuc: string;
  maKe: string;
  tang: number;
  viTriO: number;
  sucChua: number;
}

