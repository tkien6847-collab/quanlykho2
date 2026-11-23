import { useCallback, useEffect, useState, type FormEvent } from "react";
import { apiRequest } from "../services/api";
import CategoryPage from "./CategoryPage";
import DirectoryList, { type DirectoryConfig, type DirectoryField } from "./DirectoryList";
import ProfilePage from "./ProfilePage";
import type {
  Account,
  CategoryOption,
  Customer,
  DocumentLine,
  Employee,
  InventoryDocument,
  InboundDocumentDetail,
  PageId,
  Product,
  Role,
  Supplier,
  WarehouseReport,
} from "../types";

interface WorkspaceProps {
  page: PageId;
  role: Role;
  employeeId: number;
  onNavigate: (page: PageId) => void;
}

function PageHeading({ title, description }: { title: string; description: string }) {
  return (
    <div className="page-heading">
      <div>
        <div className="breadcrumb">Kho hàng <span>/</span> Workspace</div>
        <h1>{title}</h1>
        <p>{description}</p>
      </div>
    </div>
  );
}

function EmptyState({ message }: { message: string }) {
  return (
    <div className="empty-state">
      <span className="empty-icon">▤</span>
      <strong>Chưa có dữ liệu</strong>
      <p>{message}</p>
    </div>
  );
}

const emptyManagerAccount = { hoTen: "", tenDangNhap: "", matKhau: "" };
const accountsPerPage = 7;

function AccountsPage() {
  const [accounts,setAccounts]=useState<Account[]>([]); const [loading,setLoading]=useState(true); const [error,setError]=useState(""); const [notice,setNotice]=useState("");
  const [query,setQuery]=useState(""); const [page,setPage]=useState(1); const [roleValues,setRoleValues]=useState<Record<number,string>>({});
  const [managerOpen, setManagerOpen] = useState(false);
  const [manager, setManager] = useState(emptyManagerAccount);
  const [saving, setSaving] = useState(false);

  const openManagerForm = () => {
    setManager(emptyManagerAccount);
    setManagerOpen(true);
  };

  const closeManagerForm = () => {
    setManagerOpen(false);
    setManager(emptyManagerAccount);
  };
  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const list = await apiRequest<Account[]>("/api/tai-khoan");
      setAccounts(list.sort((first, second) => second.maTK - first.maTK));
      setRoleValues(Object.fromEntries(list.map((account) => [account.maTK, String(account.maVaiTro)])));
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : "Không thể tải danh sách tài khoản.");
    } finally {
      setLoading(false);
    }
  }, []);
  useEffect(()=>{void load();},[load]);
  const searchTerm = query.toLocaleLowerCase("vi");
  const filtered = accounts.filter((account) =>
    [account.hoTen, account.tenDangNhap].some((value) => value?.toLocaleLowerCase("vi").includes(searchTerm)),
  );
  const pages = Math.max(1, Math.ceil(filtered.length / accountsPerPage));
  const currentPage = Math.min(page, pages);
  const firstRow = (currentPage - 1) * accountsPerPage;
  const rows = filtered.slice(firstRow, firstRow + accountsPerPage);
  const saveRole=async(a:Account)=>{try{await apiRequest('/api/tai-khoan/'+a.maTK+'/vai-tro',{method:'PATCH',body:JSON.stringify({maVaiTro:Number(roleValues[a.maTK])})});setNotice('Đã cập nhật vai trò.');await load();}catch(e){setError(e instanceof Error?e.message:'Không cập nhật được vai trò.');}};
  const toggle=async(a:Account)=>{const trangThai=a.trangThai==='Hoạt động'?'Ngừng hoạt động':'Hoạt động';setError("");setNotice("");try{const updated=await apiRequest<Account>('/api/tai-khoan/'+a.maTK+'/trang-thai',{method:'PATCH',body:JSON.stringify({trangThai})});setAccounts(current=>current.map(item=>item.maTK===updated.maTK?updated:item));setNotice('Đã cập nhật trạng thái tài khoản.');}catch(e){setError(e instanceof Error?e.message:'Không cập nhật được trạng thái.');}};
  const createManager = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setSaving(true);
    try {
      await apiRequest("/api/tai-khoan/quan-ly", { method: "POST", body: JSON.stringify(manager) });
      closeManagerForm();
      setNotice("Đã tạo tài khoản quản lý.");
      await load();
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : "Không tạo được tài khoản.");
    } finally {
      setSaving(false);
    }
  };
  return <section className="workspace-page"><PageHeading title="Quản lý tài khoản" description=""/>{(error||notice)&&<div className={`alert ${error?'alert-error':'alert-success'}`}>{error||notice}</div>}
    <section className="panel data-panel account-list-panel"><div className="account-toolbar"><h2>Danh sách tài khoản</h2><div className="account-tools"><label className="account-search"><span>⌕</span><input value={query} onChange={e=>{setQuery(e.target.value);setPage(1);}} placeholder="Tìm theo tên" /></label><button type="button" className="button button-primary" onClick={openManagerForm}>+ Tạo tài khoản quản lý</button></div></div>
    {loading ? <div className="loading-state">Đang tải...</div> : (
      <div className="table-wrap">
        <table>
          <thead><tr><th scope="col">STT</th><th>Họ tên</th><th>Vai trò</th><th>Trạng thái</th><th>Thao tác</th></tr></thead>
          <tbody>
            {rows.map((account, index) => (
              <tr key={account.maTK}>
                <td>{firstRow + index + 1}</td>
                <td>{account.hoTen}</td>
                <td>
                  <div className="account-role-control">
                    <select
                      value={roleValues[account.maTK] || account.maVaiTro}
                      onChange={(event) => {
                        const role = event.target.value;
                        setRoleValues((current) => ({ ...current, [account.maTK]: role }));
                      }}
                    >
                      <option value="1">Admin</option>
                      <option value="2">Quản lý</option>
                      <option value="3">Nhân viên</option>
                    </select>
                    <button className="button button-secondary account-action-button" onClick={() => void saveRole(account)}>Lưu</button>
                  </div>
                </td>
                <td><span className={account.trangThai === "Hoạt động" ? "status-active" : "status-inactive"}>{account.trangThai}</span></td>
                <td>
                  <button className="button button-secondary account-action-button" onClick={() => void toggle(account)}>
                    {account.trangThai === "Hoạt động" ? "Ngừng hoạt động" : "Kích hoạt"}
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {!rows.length && <div className="loading-state">Không tìm thấy tài khoản.</div>}
      </div>
    )}
    <div className="pagination">
      <span>Trang {currentPage}/{pages} · {filtered.length} tài khoản</span>
      <button className="button button-secondary" disabled={currentPage <= 1} onClick={() => setPage(currentPage - 1)}>Trước</button>
      <button className="button button-secondary" disabled={currentPage >= pages} onClick={() => setPage(currentPage + 1)}>Sau</button>
    </div></section>
    {managerOpen && (
      <div className="modal-backdrop" role="presentation" onMouseDown={(event) => {
        if (!saving && event.target === event.currentTarget) closeManagerForm();
      }}>
        <section className="details-modal" role="dialog" aria-modal="true" aria-labelledby="manager-title">
          <div className="panel-heading">
            <h2 id="manager-title">Tạo tài khoản quản lý</h2>
            <button type="button" className="text-button" onClick={closeManagerForm} disabled={saving}>Đóng</button>
          </div>
          <form className="form-stack" autoComplete="off" onSubmit={(event) => void createManager(event)}>
            <label className="field">
              <span>Họ tên</span>
              <input maxLength={50} value={manager.hoTen}
                onChange={(event) => setManager((current) => ({ ...current, hoTen: event.target.value }))} />
            </label>
            <label className="field">
              <span>Tên đăng nhập *</span>
              <input required maxLength={50} autoComplete="off" value={manager.tenDangNhap}
                onChange={(event) => setManager((current) => ({ ...current, tenDangNhap: event.target.value }))} />
            </label>
            <label className="field">
              <span>Mật khẩu *</span>
              <input type="password" required minLength={8} autoComplete="new-password" value={manager.matKhau}
                onChange={(event) => setManager((current) => ({ ...current, matKhau: event.target.value }))} />
            </label>
            <button type="submit" className="button button-primary" disabled={saving}>
              {saving ? "Đang tạo..." : "Tạo tài khoản"}
            </button>
          </form>
        </section>
      </div>
    )}
  </section>;
}

function DocumentPage({ kind, approval = false }: { kind: "inbound" | "outbound"; approval?: boolean }) {
  const isInbound = kind === "inbound";
  const needsCustomers = !isInbound && !approval;
  const endpoint = isInbound ? "/api/phieu-nhap" : "/api/phieu-xuat";
  const dateField = isInbound ? "ngayNhap" : "ngayXuat";
  const idField = isInbound ? "maPN" : "maPX";
  const relatedField = isInbound ? "maNCC" : "maKH";
  const relatedLabel = isInbound ? "Mã nhà cung cấp" : "Mã khách hàng";
  const title = approval
    ? `Duyệt phiếu ${isInbound ? "nhập" : "xuất"} kho`
    : `Lập phiếu ${isInbound ? "nhập" : "xuất"} kho`;
  const [documents, setDocuments] = useState<InventoryDocument[]>([]);
  const [customers, setCustomers] = useState<Pick<Customer, "maKH" | "hoTen">[]>([]);
  const [values, setValues] = useState<Record<string, string>>({});
  const [lines, setLines] = useState<DocumentLine[]>([]);
  const [lineValues, setLineValues] = useState({ maHH: "", soLuong: "", donGia: "", hanSuDung: "" });
  const [selectedDetails, setSelectedDetails] = useState<InboundDocumentDetail | null>(null);
  const [detailsLoading, setDetailsLoading] = useState(false);
  const [detailsError, setDetailsError] = useState("");
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [notice, setNotice] = useState("");

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const [documentData, customerData] = await Promise.all([
        apiRequest<InventoryDocument[]>(endpoint),
        needsCustomers ? apiRequest<Pick<Customer, "maKH" | "hoTen">[]>("/api/khach-hang/lua-chon") : [],
      ]);
      setDocuments(documentData);
      setCustomers(customerData);
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : "Không thể tải phiếu.");
    } finally {
      setLoading(false);
    }
  }, [endpoint, needsCustomers]);

  useEffect(() => { void load(); }, [load]);

  const addLine = () => {
    const maHH = Number(lineValues.maHH);
    const soLuong = Number(lineValues.soLuong);
    const donGia = Number(lineValues.donGia);
    if (!Number.isInteger(maHH) || maHH <= 0 || !Number.isInteger(soLuong) || soLuong <= 0 || !Number.isFinite(donGia) || donGia < 0) {
      setError("Vui lòng nhập mã hàng, số lượng và đơn giá hợp lệ.");
      return;
    }
    if (isInbound && !lineValues.hanSuDung) {
      setError("Vui lòng nhập hạn sử dụng cho hàng hóa nhập.");
      return;
    }
    setError("");
    setLines((current) => [...current, {
      maHH,
      soLuong,
      donGia,
      ...(isInbound ? { hanSuDung: lineValues.hanSuDung } : {}),
    }]);
    setLineValues({ maHH: "", soLuong: "", donGia: "", hanSuDung: "" });
  };

  const submit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (lines.length === 0) {
      setError("Phiếu cần có ít nhất một dòng hàng.");
      return;
    }
    setSaving(true);
    setError("");
    setNotice("");
    const payload = {
      [dateField]: values[dateField],
      ghiChu: values.ghiChu,
      maNV: Number(values.maNV),
      [relatedField]: Number(values[relatedField]),
      chiTiet: lines.map((line) => isInbound
        ? { maHH: line.maHH, soLuongNhap: line.soLuong, donGiaNhap: line.donGia, hanSuDung: line.hanSuDung }
        : { maHH: line.maHH, soLuongXuat: line.soLuong, donGiaXuat: line.donGia }),
    };
    try {
      await apiRequest(endpoint, { method: "POST", body: JSON.stringify(payload) });
      setValues({});
      setLines([]);
      setNotice("Đã tạo phiếu.");
      await load();
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : "Không thể tạo phiếu.");
    } finally {
      setSaving(false);
    }
  };

  const showDetails = async (id: number) => {
    setSelectedDetails(null);
    setDetailsError("");
    setDetailsLoading(true);
    try {
      setSelectedDetails(await apiRequest<InboundDocumentDetail>(`/api/chi-tiet-phieu-nhap/${id}`));
    } catch (cause) {
      setDetailsError(cause instanceof Error ? cause.message : "Không thể tải chi tiết phiếu nhập.");
    } finally {
      setDetailsLoading(false);
    }
  };

  const updateStatus = async (document: InventoryDocument) => {
    const id = document[idField] as number | undefined;
    if (!id || document.trangThai !== "Chờ duyệt") return;
    if (!window.confirm(`Đồng ý duyệt phiếu ${isInbound ? "nhập" : "xuất"} #${id}?`)) return;
    setError("");
    setNotice("");
    try {
      await apiRequest(`${endpoint}/${id}/trang-thai`, {
        method: "PATCH",
        body: JSON.stringify({ trangThai: "Đã duyệt" }),
      });
      setNotice(`Đã duyệt phiếu #${id}.`);
      await load();
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : "Không thể cập nhật trạng thái.");
    }
  };

  return (
    <section className="workspace-page">
      <PageHeading title={title} description={approval ? "Xem phiếu đang chờ duyệt và đồng ý duyệt khi phù hợp." : "Lập phiếu mới với thông tin lấy từ nghiệp vụ thực tế."} />
      {(error || notice) && <div className={`alert ${error ? "alert-error" : "alert-success"}`} role={error ? "alert" : "status"}>{error || notice}</div>}
      <section className="panel data-panel">
        <div className="panel-heading"><div><h2>Danh sách phiếu</h2><p>{loading ? "Đang tải..." : `${documents.length} phiếu`}</p></div><button className="button button-secondary" onClick={() => void load()} disabled={loading}>↻ Làm mới</button></div>
        {loading ? <div className="loading-state">Đang tải...</div> : documents.length === 0 ? (
          <EmptyState message="Chưa có phiếu nào được trả về từ máy chủ." />
        ) : (
          <div className="table-wrap"><table>
            <thead><tr><th>Mã phiếu</th><th>Ngày</th><th>{relatedLabel}</th><th>Mã nhân viên</th><th>Tổng tiền</th><th>Trạng thái</th>{(approval || isInbound) && <th>Thao tác</th>}</tr></thead>
            <tbody>{documents.map((document, index) => {
              const id = document[idField];
              return <tr key={String(id ?? index)}>
                <td>#{id ?? "—"}</td><td>{document[dateField] ?? "—"}</td>
                <td>{document[relatedField] ?? "—"}</td><td>{document.maNV}</td><td>{document.tongTien}</td><td>{document.trangThai}</td>
                {(approval || isInbound) && <td><div className="row-actions">
                  {isInbound && typeof id === "number" && <button className="text-button" onClick={() => void showDetails(id)}>Chi tiết</button>}
                  {approval && (document.trangThai === "Chờ duyệt"
                    ? <button className="text-button" onClick={() => void updateStatus(document)}>Đồng ý duyệt</button>
                    : "—")}
                </div></td>}
              </tr>;
            })}</tbody>
          </table></div>
        )}
      </section>
      {!approval && <section className="panel document-form">
        <div className="panel-heading"><div><h2>Thông tin phiếu mới</h2><p>Các mã liên kết phải tồn tại trong dữ liệu backend</p></div></div>
        <form onSubmit={(event) => void submit(event)}>
          <div className="form-grid">
            <label className="field"><span>Ngày {isInbound ? "nhập" : "xuất"} *</span><input type="date" required value={values[dateField] ?? ""} onChange={(event) => setValues((current) => ({ ...current, [dateField]: event.target.value }))} /></label>
            <label className="field"><span>Mã nhân viên *</span><input type="number" min="1" required value={values.maNV ?? ""} onChange={(event) => setValues((current) => ({ ...current, maNV: event.target.value }))} /></label>
            {needsCustomers ? (
              <label className="field">
                <span>Khách hàng *</span>
                <select required value={values.maKH ?? ""} onChange={(event) => {
                  const maKH = event.target.value;
                  setValues((current) => ({ ...current, maKH }));
                }}>
                  <option value="">{customers.length ? "Chọn khách hàng" : "Chưa có khách hàng đang hoạt động"}</option>
                  {customers.map(({ maKH, hoTen }) => <option key={maKH} value={maKH}>{hoTen} (#{maKH})</option>)}
                </select>
              </label>
            ) : (
              <label className="field"><span>{relatedLabel} *</span><input type="number" min="1" required value={values[relatedField] ?? ""} onChange={(event) => setValues((current) => ({ ...current, [relatedField]: event.target.value }))} /></label>
            )}
            <label className="field form-span"><span>Ghi chú</span><input value={values.ghiChu ?? ""} onChange={(event) => setValues((current) => ({ ...current, ghiChu: event.target.value }))} /></label>
          </div>
          <div className="line-item-entry">
            <div className="line-item-title"><strong>Chi tiết hàng hóa</strong><span>{lines.length} dòng</span></div>
            <div className="line-entry-fields">
              <label className="field"><span>Mã hàng hóa</span><input type="number" min="1" value={lineValues.maHH} onChange={(event) => setLineValues((current) => ({ ...current, maHH: event.target.value }))} /></label>
              <label className="field"><span>Số lượng</span><input type="number" min="0.01" step="any" value={lineValues.soLuong} onChange={(event) => setLineValues((current) => ({ ...current, soLuong: event.target.value }))} /></label>
              <label className="field"><span>Đơn giá</span><input type="number" min="0" step="any" value={lineValues.donGia} onChange={(event) => setLineValues((current) => ({ ...current, donGia: event.target.value }))} /></label>
              {isInbound && <label className="field"><span>Hạn sử dụng *</span><input type="date" required value={lineValues.hanSuDung} onChange={(event) => setLineValues((current) => ({ ...current, hanSuDung: event.target.value }))} /></label>}
              <button type="button" className="button button-secondary add-line-button" onClick={addLine}>+ Thêm dòng</button>
            </div>
            {lines.length > 0 && <div className="line-list">{lines.map((line, index) => <div className="line-row" key={`${line.maHH}-${index}`}><span>Hàng #{line.maHH}</span><span>{line.soLuong} × {line.donGia}</span>{line.hanSuDung && <span>HSD: {line.hanSuDung}</span>}<button type="button" className="text-button text-danger" onClick={() => setLines((current) => current.filter((_, lineIndex) => lineIndex !== index))}>Xóa</button></div>)}</div>}
          </div>
          <p className="form-hint">Phiếu mới sẽ có trạng thái “Chờ duyệt” và được quản lý xem xét.</p>
          <button className="button button-primary" disabled={saving}>{saving ? "Đang lưu..." : "Tạo phiếu"}</button>
        </form>
      </section>}
      {isInbound && (selectedDetails || detailsLoading || detailsError) && (
        <div className="modal-backdrop" role="presentation" onClick={() => { setSelectedDetails(null); setDetailsError(""); }}>
          <section className="details-modal" role="dialog" aria-modal="true" aria-labelledby="inbound-details-title" onClick={(event) => event.stopPropagation()}>
            <div className="panel-heading">
              <div><h2 id="inbound-details-title">Chi tiết phiếu nhập #{selectedDetails?.chiTiet[0]?.id.maPN ?? ""}</h2><p>Thông tin hàng hóa và hạn sử dụng</p></div>
              <button type="button" className="text-button" onClick={() => { setSelectedDetails(null); setDetailsError(""); }}>Đóng</button>
            </div>
            {detailsLoading ? <div className="loading-state">Đang tải chi tiết...</div> : detailsError ? (
              <div className="alert alert-error" role="alert">{detailsError}</div>
            ) : selectedDetails && selectedDetails.chiTiet.length > 0 ? (
              <div className="table-wrap"><table>
                <thead><tr><th>Mã hàng hóa</th><th>Số lượng nhập</th><th>Đơn giá</th><th>HSD</th></tr></thead>
                <tbody>{selectedDetails.chiTiet.map((line) => (
                  <tr key={line.id.maHH}>
                    <td>#{line.id.maHH}</td><td>{line.soLuongNhap}</td><td>{line.donGiaNhap}</td><td>{line.hanSuDung ?? "—"}</td>
                  </tr>
                ))}</tbody>
              </table></div>
            ) : <EmptyState message="Phiếu nhập này chưa có chi tiết hàng hóa." />}
          </section>
        </div>
      )}
    </section>
  );
}

function Dashboard({ role, onNavigate }: { role: Role; onNavigate: (page: PageId) => void }) {
  const [dailySummary, setDailySummary] = useState({ inbound: "—", outbound: "—", pending: "—", lowStock: "—" });
  const [summaryReady, setSummaryReady] = useState(false);
  useEffect(() => {
    if (role !== "admin") return;
    let active = true;
    const loadDailySummary = async () => {
      const [reportResult, productsResult] = await Promise.allSettled([
        apiRequest<WarehouseReport>("/api/thong-ke"),
        apiRequest<Product[]>("/api/hang-hoa"),
      ]);
      if (!active) return;
      const report = reportResult.status === "fulfilled" ? reportResult.value : null;
      const lowStock = productsResult.status === "fulfilled" ? productsResult.value.filter((product) => product.soLuongTon <= 5).length : null;
      const format = new Intl.NumberFormat("vi-VN").format;
      setDailySummary({
        inbound: report ? format(report.soLuongNhapTrongNgay) : "—",
        outbound: report ? format(report.soLuongXuatTrongNgay) : "—",
        pending: typeof report?.soPhieuChoDuyet === "number" ? format(report.soPhieuChoDuyet) : "—",
        lowStock: lowStock === null ? "—" : String(lowStock),
      });
      setSummaryReady(true);
    };
    void loadDailySummary();
    return () => { active = false; };
  }, [role]);
  const cards: Record<Role, { page: PageId; icon: string; title: string; description: string }[]> = {
    admin: [
      { page: "accounts", icon: "♙", title: "Quản lý tài khoản", description: "Danh sách, phân quyền và tạo tài khoản quản lý." },
      { page: "categories", icon: "▤", title: "Quản lý danh mục", description: "Thêm và tra cứu danh mục hàng hóa." },
      { page: "reports", icon: "▤", title: "Thống kê & báo cáo", description: "Theo dõi lượng hàng nhập, xuất theo ngày và tháng." },
      { page: "approve-inbound", icon: "↓", title: "Duyệt phiếu nhập", description: "Duyệt phiếu và cập nhật số lượng tồn kho." },
      { page: "approve-outbound", icon: "↑", title: "Duyệt phiếu xuất", description: "Xem và duyệt các phiếu xuất kho." },
    ],
    manager: [
      { page: "reports", icon: "▤", title: "Thống kê & báo cáo", description: "Theo dõi lượng hàng nhập, xuất theo ngày và tháng." },
      { page: "employees", icon: "♙", title: "Quản lý nhân viên", description: "Thêm, cập nhật và quản lý hồ sơ nhân viên." },
      { page: "partners", icon: "♧", title: "NCC / Khách hàng", description: "Danh sách và thông tin đối tác." },
      { page: "approve-inbound", icon: "↓", title: "Duyệt phiếu nhập", description: "Kiểm tra các phiếu nhập kho." },
      { page: "approve-outbound", icon: "↑", title: "Duyệt phiếu xuất", description: "Kiểm tra các phiếu xuất kho." },
      { page: "inventory", icon: "▣", title: "Quản lý hàng hóa", description: "Danh sách, thêm, sửa và xóa hàng hóa." },
      { page: "categories", icon: "▤", title: "Quản lý danh mục", description: "Thêm và tra cứu danh mục hàng hóa." },
      { page: "stock-search", icon: "⌕", title: "Tra cứu tồn kho", description: "Tra cứu hàng theo danh mục và trạng thái tồn." },
    ],
    employee: [
      { page: "outbound", icon: "↑", title: "Lập phiếu xuất kho", description: "Tạo phiếu xuất với các dòng hàng." },
      { page: "inbound", icon: "↓", title: "Lập phiếu nhập kho", description: "Tạo phiếu nhập với các dòng hàng." },
      { page: "inventory", icon: "▣", title: "Quản lý hàng hóa", description: "Xem danh sách và cập nhật hàng hóa." },
      { page: "stock-search", icon: "⌕", title: "Tra cứu tồn kho", description: "Tra cứu hàng theo danh mục và trạng thái tồn." },
    ],
  };
  const roleName = role === "admin" ? "Quản trị viên" : role === "manager" ? "Quản lý" : "Nhân viên";

  return <section className="workspace-page">
    {role !== "admin" && <div className="welcome-banner"><div><span className="eyebrow">TRANG CHỦ</span><h1>Xin chào, {roleName}</h1></div></div>}
    {role === "admin" && <section className="daily-overview"><div className="section-title"><h2>Tổng quan theo ngày</h2></div><div className="daily-summary-grid">{[
      { label: "Nhập kho hôm nay", value: dailySummary.inbound, unit: "sản phẩm", tone: "blue", icon: "↓" },
      { label: "Xuất kho hôm nay", value: dailySummary.outbound, unit: "sản phẩm", tone: "teal", icon: "↑" },
      { label: "Phiếu chờ duyệt", value: dailySummary.pending, unit: "phiếu", tone: "violet", icon: "◷" },
      { label: "Cảnh báo tồn kho", value: dailySummary.lowStock, unit: "sản phẩm", tone: "amber", icon: "!" },
    ].map(item => <article className="daily-summary-card" key={item.label}><span className={`daily-summary-icon ${item.tone}`}>{item.icon}</span><span className="daily-summary-label">{item.label}</span><strong>{summaryReady ? item.value : "…"}</strong><small>{item.value !== "—" ? item.unit : ""}</small></article>)}</div></section>}
    <div className="section-title"><div><h2>Chức năng</h2></div><span className="role-pill">{roleName}</span></div>
    <div className="feature-grid">{cards[role].map((card,index)=><button className="feature-card" key={card.page} onClick={()=>onNavigate(card.page)}><span className={`feature-icon feature-icon-${index%4}`}>{card.icon}</span><span className="feature-arrow">↗</span><strong>{card.title}</strong><span>{card.description}</span></button>)}</div>
  </section>;
}

function ReportsPage() {
  const [report, setReport] = useState<WarehouseReport | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      setReport(await apiRequest<WarehouseReport>("/api/thong-ke"));
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : "Không thể tải thống kê.");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { void load(); }, [load]);

  const formatQuantity = (quantity: number) => new Intl.NumberFormat("vi-VN").format(quantity);
  const monthLabel = report
    ? new Date(`${report.thang}-01T00:00:00`).toLocaleDateString("vi-VN", { month: "long", year: "numeric" })
    : "";

  return <section className="workspace-page">
    <PageHeading title="Thống kê & báo cáo" description="Tổng số lượng hàng hóa nhập và xuất theo ngày, tháng." />
    <section className="panel data-panel">
      <div className="panel-heading">
        <div><h2>Sản lượng nhập / xuất</h2><p>{report ? `Cập nhật theo ngày ${report.ngay} và ${monthLabel}` : "Tổng số lượng theo phiếu đã ghi nhận"}</p></div>
        <button className="button button-secondary" onClick={() => void load()} disabled={loading}>↻ Làm mới</button>
      </div>
      {error && <div className="alert alert-error" role="alert">{error}</div>}
      {loading ? <div className="loading-state">Đang tải thống kê...</div> : report && <>
        <div className="report-grid">
          <article className="report-card">
            <span>Nhập trong ngày · {report.ngay}</span>
            <strong>{formatQuantity(report.soLuongNhapTrongNgay)}</strong>
            <small>sản phẩm</small>
          </article>
          <article className="report-card">
            <span>Xuất trong ngày · {report.ngay}</span>
            <strong>{formatQuantity(report.soLuongXuatTrongNgay)}</strong>
            <small>sản phẩm</small>
          </article>
          <article className="report-card">
            <span>Nhập trong {monthLabel}</span>
            <strong>{formatQuantity(report.soLuongNhapTrongThang)}</strong>
            <small>sản phẩm</small>
          </article>
          <article className="report-card">
            <span>Xuất trong {monthLabel}</span>
            <strong>{formatQuantity(report.soLuongXuatTrongThang)}</strong>
            <small>sản phẩm</small>
          </article>
        </div>
        <p className="form-hint">Số lượng được cộng từ chi tiết tất cả phiếu nhập và phiếu xuất theo ngày trên phiếu.</p>
      </>}
    </section>
  </section>;
}

export default function Workspace({ page, role, employeeId, onNavigate }: WorkspaceProps) {
  if (page === "dashboard") return <Dashboard role={role} onNavigate={onNavigate} />;
  if (page === "profile") return <ProfilePage />;
  if (page === "reports") return <ReportsPage />;
  if (page === "stock-search") return <StockSearchPage />;
  if (page === "accounts") return <AccountsPage />;
  if (page === "categories" && role !== "employee") return <CategoryPage />;
  if (page === "employees") return <EmployeesPage />;
  if (page === "inbound") return <InboundPage employeeId={employeeId} />;
  if (page === "outbound") return <DocumentPage key={page} kind="outbound" />;
  if (page === "approve-inbound") return <DocumentPage key={page} kind="inbound" approval />;
  if (page === "approve-outbound") return <DocumentPage key={page} kind="outbound" approval />;
  if (page === "partners") return <PartnersPage />;
  return <InventoryPage />;
}

function getLocalDateInputValue() {
  const date = new Date();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  return `${date.getFullYear()}-${month}-${day}`;
}

function InventoryPage() {
  const [products, setProducts] = useState<Product[]>([]);
  const [categories, setCategories] = useState<CategoryOption[]>([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [values, setValues] = useState({ tenHH: "", donViTinh: "", donGia: "", viTriKe: "", hinhAnh: "", tenDM: "" });
  const [editingId, setEditingId] = useState<number | null>(null);
  const [showForm, setShowForm] = useState(false);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [notice, setNotice] = useState("");

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const [products, categories] = await Promise.all([
        apiRequest<Product[]>("/api/hang-hoa"),
        apiRequest<CategoryOption[]>("/api/danh-muc/lua-chon"),
      ]);
      setProducts(products);
      setCategories(categories);
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : "Không thể tải dữ liệu hàng hóa.");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { void load(); }, [load]);

  const resetForm = () => {
    setValues({ tenHH: "", donViTinh: "", donGia: "", viTriKe: "", hinhAnh: "", tenDM: "" });
    setEditingId(null);
    setShowForm(false);
  };

  const startAdd = () => {
    setValues({ tenHH: "", donViTinh: "", donGia: "", viTriKe: "", hinhAnh: "", tenDM: "" });
    setEditingId(null);
    setError("");
    setNotice("");
    setShowForm(true);
  };

  const startEdit = (product: Product) => {
    if (product.maHH === undefined) return;
    setValues({
      tenHH: product.tenHH,
      donViTinh: product.donViTinh,
      donGia: String(product.donGia),
      viTriKe: product.viTriKe ?? "",
      hinhAnh: product.hinhAnh ?? "",
      tenDM: product.tenDM,
    });
    setEditingId(product.maHH);
    setError("");
    setNotice("");
    setShowForm(true);
  };

  const submit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setSaving(true);
    setError("");
    setNotice("");
    const existing = products.find((product) => product.maHH === editingId);
    const payload: Product = {
      tenHH: values.tenHH.trim(),
      donViTinh: values.donViTinh,
      donGia: Number(values.donGia),
      soLuongTon: existing?.soLuongTon ?? 0,
      viTriKe: values.viTriKe,
      hinhAnh: values.hinhAnh.trim() || undefined,
      trangThai: existing?.trangThai ?? "Hoạt động",
      tenDM: values.tenDM,
    };

    try {
      if (editingId === null) {
        await apiRequest("/api/hang-hoa", { method: "POST", body: JSON.stringify(payload) });
        setNotice("Đã thêm hàng hóa.");
      } else {
        await apiRequest(`/api/hang-hoa/${editingId}`, { method: "PUT", body: JSON.stringify(payload) });
        setNotice("Đã cập nhật hàng hóa.");
      }
      resetForm();
      await load();
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : "Không thể lưu hàng hóa.");
    } finally {
      setSaving(false);
    }
  };

  const remove = async (product: Product) => {
    if (product.maHH === undefined || !window.confirm(`Xóa hàng hóa "${product.tenHH}"?`)) return;
    setError("");
    setNotice("");
    try {
      await apiRequest(`/api/hang-hoa/${product.maHH}`, { method: "DELETE" });
      setNotice("Đã xóa hàng hóa.");
      await load();
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : "Không thể xóa hàng hóa.");
    }
  };

  const shelfLocations = Array.from({ length: 6 }, (_, letterIndex) =>
    Array.from({ length: 5 }, (_, shelfIndex) =>
      Array.from({ length: 4 }, (_, floorIndex) =>
        `${String.fromCharCode(65 + letterIndex)}${shelfIndex + 1}-T${floorIndex + 1}`
      )
    )
  ).flat(2);
  const selectedLocationOccupied = products.some((product) =>
    product.viTriKe?.toLocaleUpperCase("vi") === values.viTriKe.toLocaleUpperCase("vi")
  );
  const pageCount = Math.max(1, Math.ceil(products.length / 10));
  const safePage = Math.min(currentPage, pageCount);
  const visibleProducts = products.slice((safePage - 1) * 10, safePage * 10);
  const originalCategoryName = editingId === null ? undefined : products.find((product) => product.maHH === editingId)?.tenDM;

  return (
    <section className="workspace-page">
      <PageHeading title="Quản lý hàng hóa" description="Xem danh sách và thêm hàng hóa." />
      {(error || notice) && <div className={`alert ${error ? "alert-error" : "alert-success"}`} role={error ? "alert" : "status"}>{error || notice}</div>}
      <section className="panel data-panel">
        <div className="panel-heading">
          <div><h2>Danh sách hàng hóa</h2><p>{loading ? "Đang tải..." : `${products.length} sản phẩm`}</p></div>
          <div className="row-actions">
            <button type="button" className="button button-secondary" onClick={() => void load()} disabled={loading}>↻ Làm mới</button>
            <button type="button" className="button button-secondary" onClick={startAdd}>+ Thêm hàng hóa</button>
          </div>
        </div>
        {loading ? <div className="loading-state">Đang tải...</div> : products.length === 0 ? (
          <EmptyState message="Chưa có hàng hóa." />
        ) : (
          <div className="table-wrap">
            <table>
              <thead><tr><th>Mã</th><th>Tên hàng hóa</th><th>Đơn vị</th><th>Đơn giá</th><th>Tồn kho</th><th>Danh mục</th><th>Vị trí</th><th>Trạng thái</th><th>Thao tác</th></tr></thead>
              <tbody>{visibleProducts.map((product) => (
                <tr key={product.maHH}>
                  <td>{product.maSku ?? `#${product.maHH}`}</td><td>{product.tenHH}</td><td>{product.donViTinh}</td><td>{product.donGia}</td>
                  <td>{product.soLuongTon}</td><td>{product.tenDM}</td><td>{product.viTriKe || "—"}</td><td>{product.trangThai}</td>
                  <td><div className="row-actions">
                    <button type="button" className="text-button" onClick={() => startEdit(product)}>Sửa</button>
                    <button type="button" className="text-button text-danger" onClick={() => void remove(product)}>Xóa</button>
                  </div></td>
                </tr>
              ))}</tbody>
            </table>
          </div>
        )}
        {pageCount > 1 && <div className="pagination">
          <button type="button" className="button button-secondary" disabled={safePage === 1} onClick={() => setCurrentPage((page) => page - 1)}>Trước</button>
          <span>Trang {safePage} / {pageCount}</span>
          <button type="button" className="button button-secondary" disabled={safePage === pageCount} onClick={() => setCurrentPage((page) => page + 1)}>Sau</button>
        </div>}
      </section>
      {showForm && <section className="panel form-panel inventory-form-panel">
        <div className="panel-heading">
          <div><h2>{editingId === null ? "Thêm hàng hóa" : "Cập nhật hàng hóa"}</h2></div>
          <button type="button" className="text-button" onClick={resetForm}>Đóng</button>
        </div>
        <form className="form-grid" onSubmit={(event) => void submit(event)}>
          <label className="field">
            <span>Tên hàng hóa *</span>
            <input required maxLength={100} value={values.tenHH} onChange={(event) => setValues((current) => ({ ...current, tenHH: event.target.value }))} />
          </label>
          <label className="field">
            <span>Đơn vị tính *</span>
            <select required value={values.donViTinh} onChange={(event) => setValues((current) => ({ ...current, donViTinh: event.target.value }))}>
              <option value="">Chọn đơn vị tính</option>
              {["Cái", "Chiếc", "Bộ", "Hộp", "Thùng", "Gói", "Chai", "Lít", "Kg", "Mét", "Cuộn"].map((unit) => <option key={unit} value={unit}>{unit}</option>)}
            </select>
          </label>
          <label className="field">
            <span>Đơn giá *</span>
            <input type="number" min="0" step="any" required value={values.donGia} onChange={(event) => setValues((current) => ({ ...current, donGia: event.target.value }))} />
          </label>
          <label className="field">
            <span>Danh mục *</span>
            <select required value={values.tenDM} onChange={(event) => setValues((current) => ({ ...current, tenDM: event.target.value }))}>
              <option value="">Chọn danh mục</option>
              {originalCategoryName && !categories.some(({ tenDM }) => tenDM === originalCategoryName) && (
                <option value={originalCategoryName}>{originalCategoryName} — ngừng hoạt động</option>
              )}
              {categories.map(({ maDM, tenDM }) => <option key={maDM} value={tenDM}>{tenDM}</option>)}
            </select>
          </label>
          <label className="field">
            <span>Vị trí kệ *</span>
            <select required value={values.viTriKe} onChange={(event) => setValues((current) => ({ ...current, viTriKe: event.target.value }))}>
              <option value="">Chọn vị trí</option>
              {shelfLocations.map((location) => {
                const occupied = products.some((product) =>
                  product.viTriKe?.toLocaleUpperCase("vi") === location.toLocaleUpperCase("vi")
                  && product.maHH !== editingId
                );
                return <option key={location} value={location} disabled={occupied}>{location} — {occupied ? "Đã có sản phẩm" : "Trống"}</option>;
              })}
            </select>
            {values.viTriKe && <small className={selectedLocationOccupied ? "location-occupied" : "location-empty"}>
              {selectedLocationOccupied ? "Đã có sản phẩm" : "Trống"}
            </small>}
          </label>
          <label className="field">
            <span>Hình ảnh</span>
            <input maxLength={255} placeholder="Đường dẫn hình ảnh" value={values.hinhAnh} onChange={(event) => setValues((current) => ({ ...current, hinhAnh: event.target.value }))} />
          </label>
          <div className="form-span inventory-form-actions">
            <button type="button" className="button button-secondary" onClick={resetForm}>Hủy</button>
            <button className="button button-primary" disabled={saving}>{saving ? "Đang lưu..." : editingId === null ? "Thêm hàng hóa" : "Lưu thay đổi"}</button>
          </div>
        </form>
      </section>}
    </section>
  );
}

function InboundPage({ employeeId }: { employeeId: number }) {
  const [products, setProducts] = useState<Product[]>([]);
  const [suppliers, setSuppliers] = useState<Supplier[]>([]);
  const [selectedProduct, setSelectedProduct] = useState<Product | null>(null);
  const [currentPage, setCurrentPage] = useState(1);
  const [values, setValues] = useState({ ngayNhap: getLocalDateInputValue(), maNCC: "", ghiChu: "", soLuong: "1", donGia: "" });
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [notice, setNotice] = useState("");

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const [productData, supplierData] = await Promise.all([
        apiRequest<Product[]>("/api/hang-hoa"),
        apiRequest<Supplier[]>("/api/nha-cung-cap"),
      ]);
      setProducts(productData);
      setSuppliers(supplierData.filter((supplier) => supplier.trangThai === "Hoạt động"));
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : "Không thể tải hàng hóa và nhà cung cấp.");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { void load(); }, [load]);

  const pageCount = Math.max(1, Math.ceil(products.length / 10));
  const safePage = Math.min(currentPage, pageCount);
  const visibleProducts = products.slice((safePage - 1) * 10, safePage * 10);

  const openInboundForm = (product: Product) => {
    setSelectedProduct(product);
    setValues({
      ngayNhap: getLocalDateInputValue(),
      maNCC: "",
      ghiChu: "",
      soLuong: "1",
      donGia: String(product.donGia),
    });
    setError("");
    setNotice("");
  };

  const submit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!selectedProduct?.maHH) {
      setError("Vui lòng chọn hàng hóa cần nhập.");
      return;
    }
    setSaving(true);
    setError("");
    setNotice("");
    try {
      await apiRequest("/api/phieu-nhap", {
        method: "POST",
        body: JSON.stringify({
          ngayNhap: values.ngayNhap,
          ghiChu: values.ghiChu,
          maNV: employeeId,
          maNCC: Number(values.maNCC),
          chiTiet: [{
            maHH: selectedProduct.maHH,
            soLuongNhap: Number(values.soLuong),
            donGiaNhap: Number(values.donGia),
          }],
        }),
      });
      setNotice("Đã gửi phiếu nhập, tồn kho sẽ cập nhật sau khi được duyệt.");
      setSelectedProduct(null);
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : "Không thể tạo phiếu nhập.");
    } finally {
      setSaving(false);
    }
  };

  return (
    <section className="workspace-page">
      <PageHeading title="Lập phiếu nhập kho" description="Chọn hàng hóa để lập phiếu nhập." />
      {(error || notice) && <div className={`alert ${error ? "alert-error" : "alert-success"}`} role={error ? "alert" : "status"}>{error || notice}</div>}
      <section className="panel data-panel">
        <div className="panel-heading">
          <div><h2>Danh sách hàng hóa</h2><p>{loading ? "Đang tải..." : `${products.length} sản phẩm`}</p></div>
          <button type="button" className="button button-secondary" onClick={() => void load()} disabled={loading}>↻ Làm mới</button>
        </div>
        {loading ? <div className="loading-state">Đang tải...</div> : products.length === 0 ? <EmptyState message="Chưa có hàng hóa." /> : (
          <div className="table-wrap">
            <table>
              <thead><tr><th>Mã hàng</th><th>Tên hàng hóa</th><th>Đơn vị</th><th>Tồn kho</th><th>Thao tác</th></tr></thead>
              <tbody>{visibleProducts.map((product) => (
                <tr key={product.maHH}>
                  <td>{product.maSku ?? `#${product.maHH}`}</td>
                  <td>{product.tenHH}</td><td>{product.donViTinh}</td><td>{product.soLuongTon}</td>
                  <td><button type="button" className="text-button" onClick={() => openInboundForm(product)}>Lập phiếu nhập</button></td>
                </tr>
              ))}</tbody>
            </table>
          </div>
        )}
        {pageCount > 1 && <div className="pagination">
          <button type="button" className="button button-secondary" disabled={safePage === 1} onClick={() => setCurrentPage((page) => page - 1)}>Trước</button>
          <span>Trang {safePage} / {pageCount}</span>
          <button type="button" className="button button-secondary" disabled={safePage === pageCount} onClick={() => setCurrentPage((page) => page + 1)}>Sau</button>
        </div>}
      </section>
      {selectedProduct && <section className="panel form-panel inventory-form-panel">
        <div className="panel-heading">
          <div><h2>Lập phiếu nhập: {selectedProduct.tenHH}</h2><p>{selectedProduct.maSku ?? `#${selectedProduct.maHH}`}</p></div>
          <button type="button" className="text-button" onClick={() => setSelectedProduct(null)}>Đóng</button>
        </div>
        <form className="form-grid" onSubmit={(event) => void submit(event)}>
          <label className="field"><span>Ngày nhập *</span><input type="date" required value={values.ngayNhap} onChange={(event) => setValues((current) => ({ ...current, ngayNhap: event.target.value }))} /></label>
          <label className="field"><span>Mã nhân viên</span><input value={employeeId} readOnly /></label>
          <label className="field"><span>Nhà cung cấp *</span>
            <select required value={values.maNCC} onChange={(event) => setValues((current) => ({ ...current, maNCC: event.target.value }))}>
              <option value="">Chọn nhà cung cấp</option>
              {suppliers.map((supplier) => <option key={supplier.maNCC} value={supplier.maNCC}>{supplier.tenNCC}{supplier.maNccCode ? ` (${supplier.maNccCode})` : ""}</option>)}
            </select>
          </label>
          <label className="field"><span>Số lượng *</span><input type="number" min="1" step="1" required value={values.soLuong} onChange={(event) => setValues((current) => ({ ...current, soLuong: event.target.value }))} /></label>
          <label className="field"><span>Đơn giá *</span><input type="number" min="0" step="any" required value={values.donGia} onChange={(event) => setValues((current) => ({ ...current, donGia: event.target.value }))} /></label>
          <label className="field form-span"><span>Ghi chú</span><input maxLength={200} value={values.ghiChu} onChange={(event) => setValues((current) => ({ ...current, ghiChu: event.target.value }))} /></label>
          <div className="form-span inventory-form-actions">
            <button type="button" className="button button-secondary" onClick={() => setSelectedProduct(null)}>Hủy</button>
            <button className="button button-primary" disabled={saving}>{saving ? "Đang gửi..." : "Gửi phiếu nhập"}</button>
          </div>
        </form>
      </section>}
    </section>
  );
}

function StockSearchPage() {
  const [tenDanhMuc, setTenDanhMuc] = useState("");
  const [trangThai, setTrangThai] = useState("Còn hàng");
  const [products, setProducts] = useState<Product[] | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const search = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setLoading(true);
    setError("");
    try {
      const params = new URLSearchParams({ tenDanhMuc: tenDanhMuc.trim(), trangThai });
      setProducts(await apiRequest<Product[]>(`/api/hang-hoa/tra-cuu?${params.toString()}`));
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : "Không thể tra cứu tồn kho.");
      setProducts(null);
    } finally {
      setLoading(false);
    }
  };

  return <section className="workspace-page">
    <PageHeading title="Tra cứu hàng tồn kho" description="Tìm hàng hóa theo tên danh mục và tình trạng tồn kho." />
    <section className="panel form-panel">
      <form className="form-grid" onSubmit={(event) => void search(event)}>
        <label className="field">
          <span>Tên danh mục *</span>
          <input required maxLength={100} value={tenDanhMuc} onChange={(event) => setTenDanhMuc(event.target.value)} placeholder="Nhập tên danh mục" />
        </label>
        <label className="field">
          <span>Trạng thái tồn kho *</span>
          <select required value={trangThai} onChange={(event) => setTrangThai(event.target.value)}>
            <option value="Còn hàng">Còn hàng (&gt; 5)</option>
            <option value="Sắp hết">Sắp hết (1–5)</option>
            <option value="Hết hàng">Hết hàng (0)</option>
          </select>
        </label>
        <div className="form-span">
          <button className="button button-primary" disabled={loading}>
            {loading ? "Đang tra cứu..." : "Tra cứu"}
          </button>
        </div>
      </form>
    </section>
    {error && <div className="alert alert-error" role="alert">{error}</div>}
    {products !== null && <section className="panel data-panel">
      <div className="panel-heading">
        <div><h2>Kết quả tra cứu</h2><p>{products.length} mặt hàng</p></div>
      </div>
      {products.length === 0 ? <EmptyState message="Không tìm thấy hàng hóa phù hợp với danh mục và trạng thái đã chọn." /> : (
        <div className="table-wrap">
          <table>
            <thead><tr><th>Mã</th><th>Tên hàng hóa</th><th>Danh mục</th><th>Đơn vị</th><th>Số lượng tồn</th><th>Trạng thái tồn kho</th></tr></thead>
            <tbody>{products.map((product) => (
              <tr key={product.maHH}>
                <td>{product.maSku ?? `#${product.maHH}`}</td>
                <td>{product.tenHH}</td>
                <td>{product.tenDM}</td>
                <td>{product.donViTinh}</td>
                <td>{product.soLuongTon}</td>
                <td>{product.soLuongTon === 0 ? "Hết hàng" : product.soLuongTon <= 5 ? "Sắp hết" : "Còn hàng"}</td>
              </tr>
            ))}</tbody>
          </table>
        </div>
      )}
    </section>}
  </section>;
}

const employeesDirectory: DirectoryConfig<Employee> = {
  noun: "nhân viên",
  endpoint: "/api/nhan-vien",
  createEndpoint: "/api/nhan-vien/tai-khoan",
  createTitle: "Tạo tài khoản nhân viên",
  getId: (employee) => employee.maNV,
  getName: (employee) => [employee.hoTen, employee.tenDangNhap].filter(Boolean).join(" "),
  getStatus: (employee) => employee.trangThai,
  fields: (employees, values) => {
    const fields: DirectoryField[] = [];
    const unlinked = employees.filter((employee) => employee.trangThai === null);
    if (unlinked.length > 0) {
      fields.push({
        name: "maNV",
        label: "Hồ sơ nhân viên",
        type: "select",
        emptyOption: "Tạo hồ sơ mới",
        options: unlinked.map((employee) => ({
          value: employee.maNV,
          label: employee.hoTen ? `${employee.hoTen} (#${employee.maNV})` : `Hồ sơ #${employee.maNV}`,
        })),
      });
    }
    if (!values.maNV) fields.push({ name: "hoTen", label: "Họ tên", type: "text", maxLength: 50 });
    fields.push(
      { name: "tenDangNhap", label: "Tên đăng nhập", type: "text", maxLength: 50, required: true, autoComplete: "off" },
      { name: "matKhau", label: "Mật khẩu", type: "password", minLength: 8, required: true, autoComplete: "new-password" },
    );
    return fields;
  },
  columns: [
    { key: "hoTen", label: "Họ tên" },
    { key: "tenDangNhap", label: "Tên đăng nhập" },
    { key: "chucVu", label: "Chức vụ" },
    { key: "soDienThoai", label: "Điện thoại" },
    { key: "email", label: "Email" },
  ],
};

function EmployeesPage() {
  return (
    <section className="workspace-page">
      <div className="page-heading"><h1>Quản lý nhân viên</h1></div>
      <DirectoryList config={employeesDirectory} />
    </section>
  );
}

const suppliersDirectory: DirectoryConfig<Supplier> = {
  noun: "nhà cung cấp",
  endpoint: "/api/nha-cung-cap",
  getId: (supplier) => supplier.maNCC,
  getName: (supplier) => supplier.tenNCC,
  getStatus: (supplier) => supplier.trangThai,
  fields: [
    { name: "tenNCC", label: "Tên nhà cung cấp", type: "text", maxLength: 100, required: true },
    { name: "soDienThoai", label: "Số điện thoại", type: "tel", maxLength: 20, required: true },
    { name: "email", label: "Email", type: "email", maxLength: 100, required: true },
    { name: "diaChi", label: "Địa chỉ", type: "text", maxLength: 200, required: true },
  ],
  columns: [
    { key: "tenNCC", label: "Tên nhà cung cấp" },
    { key: "soDienThoai", label: "Điện thoại" },
    { key: "email", label: "Email" },
    { key: "diaChi", label: "Địa chỉ" },
  ],
};

const customersDirectory: DirectoryConfig<Customer> = {
  noun: "khách hàng",
  endpoint: "/api/khach-hang",
  getId: (customer) => customer.maKH,
  getName: (customer) => customer.hoTen,
  getStatus: (customer) => customer.trangThai,
  fields: [
    { name: "hoTen", label: "Họ tên khách hàng", type: "text", maxLength: 30, required: true },
    { name: "soDienThoai", label: "Số điện thoại", type: "tel", maxLength: 20, required: true },
    { name: "email", label: "Email", type: "email", maxLength: 100, required: true },
    { name: "diaChi", label: "Địa chỉ", type: "text", maxLength: 200, required: true },
  ],
  columns: [
    { key: "hoTen", label: "Họ tên" },
    { key: "soDienThoai", label: "Điện thoại" },
    { key: "email", label: "Email" },
    { key: "diaChi", label: "Địa chỉ" },
  ],
};

function PartnersPage() {
  const [selected, setSelected] = useState<"suppliers" | "customers">("suppliers");
  return <section className="workspace-page">
    <div className="page-heading"><h1>Nhà cung cấp / Khách hàng</h1></div>
    <div className="tabs" role="tablist" aria-label="Loại đối tác">
      <button role="tab" aria-selected={selected === "suppliers"} className={selected === "suppliers" ? "tab-active" : ""} onClick={() => setSelected("suppliers")}>Nhà cung cấp</button>
      <button role="tab" aria-selected={selected === "customers"} className={selected === "customers" ? "tab-active" : ""} onClick={() => setSelected("customers")}>Khách hàng</button>
    </div>
    {selected === "suppliers"
      ? <DirectoryList key="suppliers" config={suppliersDirectory} />
      : <DirectoryList key="customers" config={customersDirectory} />}
  </section>;
}
