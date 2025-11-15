import { Component, Input, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-file-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './file-list.component.html',
  styleUrl: './file-list.component.css',
})
export class FileListComponent {
  @Input() files: any[] = [];
  @Output() fileDeleted = new EventEmitter<string>();

  deleteFile(fileKey: string) {
    if (confirm('Are you sure you want to delete this file?')) {
      // Here you would typically call a service to delete the file from the server
      console.log(`File with key ${fileKey} deleted.`);
    this.fileDeleted.emit(fileKey);
    }
  }

  formatFileSize(bytes: number): string {
    if (bytes < 1024) return `${bytes} B`;
    else if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(2)} KB}`;
    else return `${(bytes / (1024 * 1024)).toFixed(2)} MB`;
  }
}
