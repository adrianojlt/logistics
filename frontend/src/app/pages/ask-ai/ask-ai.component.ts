import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AiService } from '../../services/ai.service';

@Component({
  selector: 'app-ask-ai',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatExpansionModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './ask-ai.component.html',
  styleUrl: './ask-ai.component.css',
})
export class AskAiComponent {

  question = '';
  isLoading = false;
  generatedSql: string | null = null;
  answer: string | null = null;
  errorMessage: string | null = null;

  constructor(private aiService: AiService) {}

  ask(): void {
    if (!this.question.trim()) {
      return;
    }

    this.isLoading = true;
    this.generatedSql = null;
    this.answer = null;
    this.errorMessage = null;

    this.aiService.query(this.question).subscribe({
      next: (response) => {
        const data = response.data;
        this.generatedSql = data.generatedSql;

        if (data.success) {
          this.answer = data.answer;
        } else {
          this.errorMessage = data.errorMessage;
        }

        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to reach the AI service. Is the backend running?';
        this.isLoading = false;
      }
    });
  }
}
