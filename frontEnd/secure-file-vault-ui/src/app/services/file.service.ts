import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class FileService {
  private apiUrl = 'http://localhost:8080/api/files';
  constructor(private http: HttpClient) {}

  getMyFiles(): Observable<any[]> {
        const token = localStorage.getItem('token'); // same key used in login
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    return this.http.get<any[]>(`${this.apiUrl}/my-files`, { headers });
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
