import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class FileService {
  private apiUrl = 'http://localhost:8080/api/files';
  constructor(private http: HttpClient) {}

  getMyFiles(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/my-files`);
  }

  uploadFiles(formData: FormData): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/upload`, formData);
  }

  deleteFile(key: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/delete/${key}`);
  }

  downloadFile(key: string): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/download/${key}`, { responseType: 'blob' });
  }
  
}
