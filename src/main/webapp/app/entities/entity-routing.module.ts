import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'file',
        data: { pageTitle: 'linterApp.file.home.title' },
        loadChildren: () => import('./file/file.module').then(m => m.FileModule),
      },
      {
        path: 'version-file',
        data: { pageTitle: 'linterApp.versionFile.home.title' },
        loadChildren: () => import('./version-file/version-file.module').then(m => m.VersionFileModule),
      },
      {
        path: 'bag',
        data: { pageTitle: 'linterApp.bag.home.title' },
        loadChildren: () => import('./bag/bag.module').then(m => m.BagModule),
      },
      {
        path: 'task',
        data: { pageTitle: 'linterApp.task.home.title' },
        loadChildren: () => import('./task/task.module').then(m => m.TaskModule),
      },
      {
        path: 'post',
        data: { pageTitle: 'linterApp.post.home.title' },
        loadChildren: () => import('./post/post.module').then(m => m.PostModule),
      },
      {
        path: 'rules',
        data: { pageTitle: 'linterApp.rules.home.title' },
        loadChildren: () => import('./rules/rules.module').then(m => m.RulesModule),
      },
      {
        path: 'support',
        data: { pageTitle: 'linterApp.support.home.title' },
        loadChildren: () => import('./support/support.module').then(m => m.SupportModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
