export function normalizeSearch(value: string) {
  return value.trim().toLocaleLowerCase("vi").normalize("NFD").replace(/\p{M}/gu, "").replace(/đ/g, "d");
}
