#!/usr/bin/env python3

"""
Hero Image Downloader for MLBB Drafting Simulator
Downloads placeholder images for all heroes in the database
"""

import asyncio
import os
from pathlib import Path
import subprocess
import sys
from src.data.hero_database import HeroDatabase


class HeroImageDownloader:
    def __init__(self):
        self.assets_dir = Path("assets/heroes")
        self.assets_dir.mkdir(parents=True, exist_ok=True)
    
    def download_image(self, url: str, filename: str) -> bool:
        """Download an image using PowerShell Invoke-WebRequest"""
        try:
            output_path = self.assets_dir / filename
            
            # Use PowerShell to download the image
            cmd = [
                "powershell", "-Command",
                f"Invoke-WebRequest -Uri '{url}' -OutFile '{output_path}'"
            ]
            
            result = subprocess.run(cmd, capture_output=True, text=True)
            
            if result.returncode == 0:
                print(f"✅ Downloaded: {filename}")
                return True
            else:
                print(f"❌ Failed to download {filename}: {result.stderr}")
                return False
                
        except Exception as e:
            print(f"❌ Error downloading {filename}: {e}")
            return False
    
    async def download_all_hero_images(self):
        """Download images for all heroes in the database"""
        print("🎮 MLBB Hero Image Downloader")
        print("=============================")
        
        # Initialize hero database
        hero_db = HeroDatabase()
        await hero_db.initialize()
        
        heroes = hero_db.get_all_heroes()
        print(f"📊 Found {len(heroes)} heroes to download images for")
        
        success_count = 0
        total_count = len(heroes)
        
        for hero in heroes:
            hero_id = hero['id']
            hero_name = hero['name']
            
            # Create filename
            filename = f"{hero_id}.jpg"
            
            print(f"\n🎯 Downloading image for {hero_name} ({hero_id})...")
            
            # Try different placeholder services based on hero role
            role_colors = {
                'Tank': '1f77b4',      # Blue
                'Fighter': 'ff7f0e',   # Orange  
                'Assassin': 'd62728',  # Red
                'Mage': '9467bd',      # Purple
                'Marksman': '2ca02c',  # Green
                'Support': 'e377c2'    # Pink
            }
            
            role = hero.get('role', 'Tank')
            color = role_colors.get(role, '1f77b4')
            
            # Use picsum.photos with a seed based on hero name for consistent images
            seed = hash(hero_name) % 1000
            url = f"https://picsum.photos/seed/{seed}/300/400"
            
            # Alternative: Use a placeholder service with role colors
            # url = f"https://via.placeholder.com/300x400/{color}/ffffff.jpg?text={hero_name}"
            
            if self.download_image(url, filename):
                success_count += 1
            
            # Small delay to be respectful to the API
            await asyncio.sleep(0.5)
        
        print(f"\n📊 Download Summary:")
        print(f"✅ Successfully downloaded: {success_count}/{total_count} images")
        print(f"📁 Images stored in: {self.assets_dir.absolute()}")
        
        if success_count == total_count:
            print("🎉 All hero images downloaded successfully!")
        else:
            print("⚠️  Some images failed to download. Check the errors above.")


async def main():
    """Main entry point"""
    downloader = HeroImageDownloader()
    await downloader.download_all_hero_images()


if __name__ == "__main__":
    asyncio.run(main())