import {test,expect} from '@playwright/test';test('loads editor',async({page})=>{await page.goto('/');await expect(page.getByText('Vietsub Pro')).toBeVisible()});
