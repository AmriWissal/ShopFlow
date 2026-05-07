import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Category } from '../../../core/models';
import { CategoryService } from '../../../core/services/category.service';
import { MatIconModule } from '@angular/material/icon';
import { FormsModule, ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-category-management',
  standalone: true,
  imports: [CommonModule, MatIconModule, FormsModule, ReactiveFormsModule],
  templateUrl: './category-management.component.html',
  styleUrls: ['./category-management.component.css']
})
export class CategoryManagementComponent implements OnInit {
  private categoryService = inject(CategoryService);
  private fb = inject(FormBuilder);
  private snackBar = inject(MatSnackBar);

  categories: Category[] = [];
  showModal = false;
  editingId: number | null = null;

  categoryForm = this.fb.group({
    name: ['', Validators.required],
    description: ['', Validators.required]
  });

  ngOnInit() {
    this.loadCategories();
  }

  loadCategories() {
    this.categoryService.getCategories().subscribe(res => this.categories = res);
  }

  openModal() {
    this.editingId = null;
    this.categoryForm.reset();
    this.showModal = true;
  }

  editCategory(cat: Category) {
    this.editingId = cat.id;
    this.categoryForm.patchValue({
      name: cat.name,
      description: cat.description
    });
    this.showModal = true;
  }

  closeModal() {
    this.showModal = false;
  }

  saveCategory() {
    if (this.categoryForm.valid) {
      const obs = this.editingId 
        ? this.categoryService.updateCategory(this.editingId, this.categoryForm.value as Partial<Category>)
        : this.categoryService.createCategory(this.categoryForm.value as Partial<Category>);

      obs.subscribe({
        next: () => {
          this.snackBar.open(`✅ Catégorie ${this.editingId ? 'mise à jour' : 'créée'} !`, 'Fermer', { 
            duration: 3000,
            panelClass: ['success-snackbar'],
            horizontalPosition: 'end',
            verticalPosition: 'top'
          });
          this.loadCategories();
          this.closeModal();
        }
      });
    }
  }

  deleteCategory(id: number) {
    if (confirm('Supprimer cette catégorie ? Cela pourrait affecter les produits qui y sont rattachés.')) {
      this.categoryService.deleteCategory(id).subscribe(() => {
        this.loadCategories();
        this.snackBar.open('✅ Catégorie supprimée', 'Fermer', { 
          duration: 3000,
          panelClass: ['success-snackbar'],
          horizontalPosition: 'end',
          verticalPosition: 'top'
        });
      });
    }
  }
}