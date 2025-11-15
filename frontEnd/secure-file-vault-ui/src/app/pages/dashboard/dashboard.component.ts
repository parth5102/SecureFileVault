import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FileService } from '../../services/file.service';
import { UploadFormComponent } from '../../components/upload-form/upload-form.component';
import { FileListComponent } from '../../components/file-list/file-list.component';


@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, UploadFormComponent, FileListComponent],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

  files: any[] = [];
  constructor(private fileService: FileService, private router: Router) {}

  ngOnInit() {
    this.loadFiles();
  }

  loadFiles() {
    this.fileService.getMyFiles().subscribe({
      next: (data) => {
        
        this.files = data.map((file: any) => ({
          id: file._id,
          key:file.key,
          filename: file.filename,
          sizeBytes: file.sizeBytes,
          contentType: file.contentType,
          uploadedAt: file.uploadedAt,
          owner: file.owner
        }));
      },
      error: (err) => console.error('Error loading files', err)

    });
  }

  onUploadSuccess() {
    console.log('Upload successful, reloading file list.');
    this.loadFiles();
  }

  onFileDeleted(fileKey: string) {
    this.fileService.deleteFile(fileKey).subscribe({
      next: () => {
        console.log(`File with key ${fileKey} deleted successfully.`);
        this.files = this.files.filter(file => file.key !== fileKey);
      },
      error: (err) => {
        console.error('Error deleting file:', err);
      }
    });
  }

  logout() {
    localStorage.removeItem('token');
    this.router.navigate(['/login']);
  }
}
