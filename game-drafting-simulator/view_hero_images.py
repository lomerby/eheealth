#!/usr/bin/env python3

"""
Hero Image Viewer - Simple utility to view downloaded hero images
"""

import asyncio
import os
from pathlib import Path
from src.data.hero_database import HeroDatabase


async def main():
    """Display information about downloaded hero images"""
    print("🖼️  MLBB Hero Images Viewer")
    print("===========================")
    
    # Initialize hero database
    hero_db = HeroDatabase()
    await hero_db.initialize()
    
    heroes = hero_db.get_all_heroes()
    assets_dir = Path("assets/heroes")
    
    print(f"\n📊 Found {len(heroes)} heroes with images:")
    print("-" * 50)
    
    for i, hero in enumerate(heroes, 1):
        image_path = Path(hero['image_path'])
        
        # Check if image file exists
        exists = "✅" if image_path.exists() else "❌"
        
        # Get file size if it exists
        size = ""
        if image_path.exists():
            size_bytes = image_path.stat().st_size
            size = f" ({size_bytes:,} bytes)"
        
        print(f"{i:2}. {hero['name']:12} ({hero['role']:9}) {exists} {image_path.name}{size}")
    
    print("-" * 50)
    print(f"📁 Images directory: {assets_dir.absolute()}")
    
    # Count actual files in directory
    if assets_dir.exists():
        image_files = list(assets_dir.glob("*.jpg"))
        print(f"📸 Total image files: {len(image_files)}")
    else:
        print("❌ Images directory not found")


if __name__ == "__main__":
    asyncio.run(main())