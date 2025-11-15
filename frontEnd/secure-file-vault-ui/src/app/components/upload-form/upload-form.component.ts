import { Component, EventEmitter, Output } from '@angular/core';
import { FileService } from '../../services/file.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-upload-form',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './upload-form.component.html',
  styleUrl: './upload-form.component.css',
})
export class UploadFormComponent {

  @Output() fileUploaded = new EventEmitter<void>();  // 🔥 notify dashboard

  selectedFile: File | null = null;   // 🔥 fixed typo
  message = '';

  constructor(private fileService: FileService) {}

  // 🔥 handle file input
  onFileSelected(event: any) {
    this.selectedFile = event.target.files[0];
  }

  // 🔥 upload only — no file loading
  uploadFile() {
    if (!this.selectedFile) {
      alert('Please select a file first!');
      return;
    }

    const formData = new FormData();
    formData.append('file', this.selectedFile);

    this.fileService.uploadFiles(formData).subscribe({
      next: (response) => {
        this.message = response.message || 'File uploaded successfully!';
        this.fileUploaded.emit();  // 🔥 tell dashboard to refresh
      },
      error: (err) => {
        console.error('Upload error:', err);
        this.message = 'Upload failed!';
      }
    });
  }
}
