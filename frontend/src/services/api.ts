// Use the same origin as the page so the app works through ngrok or a LAN URL.
// Set VITE_API_BASE_URL only when the API is hosted on a different origin.
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "";

export class ApiError extends Error {
  constructor(message: string, public readonly status: number) {
    super(message);
    this.name = "ApiError";
  }
}

export async function apiRequest<T>(path: string, init?: RequestInit): Promise<T> {
  let response: Response;
  const token = path === "/api/auth/login" ? null : sessionStorage.getItem("accessToken");
  try {
    response = await fetch(`${API_BASE_URL}${path}`, {
      ...init,
      headers: {
        ...(init?.body ? { "Content-Type": "application/json" } : {}),
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
        ...init?.headers,
      },
    });
  } catch {
    throw new Error(`Không thể kết nối máy chủ tại ${API_BASE_URL}.`);
  }

  const responseText = await response.text();

  if (!response.ok) {
    let problem: { message?: string } | null = null;
    try {
      problem = responseText ? JSON.parse(responseText) as { message?: string } : null;
    } catch {
      // Keep the status-based error message below for non-JSON responses.
    }
    const message = typeof problem?.message === "string" ? problem.message : "";
    if (message) throw new ApiError(message, response.status);
    if (response.status === 401 && path === "/api/auth/login") {
      throw new ApiError("Tên đăng nhập hoặc mật khẩu không đúng.", response.status);
    }
    if (response.status === 401) {
      throw new ApiError("Phiên đăng nhập không còn hợp lệ. Vui lòng đăng nhập lại.", response.status);
    }
    if (response.status === 403) {
      throw new ApiError("Tài khoản không có quyền thực hiện thao tác này.", response.status);
    }
    throw new ApiError(`Yêu cầu thất bại (${response.status}).`, response.status);
  }

  if (!responseText.trim()) return undefined as T;
  try {
    return JSON.parse(responseText) as T;
  } catch {
    throw new ApiError("Phản hồi từ máy chủ không phải JSON hợp lệ.", response.status);
  }
}
