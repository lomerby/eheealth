"""
HeroDatabase - Manages MLBB hero data and metadata
"""

import random
from typing import List, Dict, Optional


class HeroDatabase:
    def __init__(self):
        self.heroes: Dict[str, Dict] = {}
        self.roles = ['Tank', 'Fighter', 'Assassin', 'Mage', 'Marksman', 'Support']

    async def initialize(self):
        """Initialize the hero database with MLBB heroes"""
        print('📊 Loading MLBB heroes...')
        
        # Sample heroes data - in a real implementation, this would come from a JSON file or API
        heroes_data = [
            {'id': 'tigreal', 'name': 'Tigreal', 'role': 'Tank', 'specialty': 'Crowd Control'},
            {'id': 'alucard', 'name': 'Alucard', 'role': 'Fighter', 'specialty': 'Damage'},
            {'id': 'gusion', 'name': 'Gusion', 'role': 'Assassin', 'specialty': 'Burst'},
            {'id': 'kagura', 'name': 'Kagura', 'role': 'Mage', 'specialty': 'Burst/Crowd Control'},
            {'id': 'granger', 'name': 'Granger', 'role': 'Marksman', 'specialty': 'Damage'},
            {'id': 'angela', 'name': 'Angela', 'role': 'Support', 'specialty': 'Crowd Control/Support'},
            {'id': 'kaja', 'name': 'Kaja', 'role': 'Support', 'specialty': 'Crowd Control'},
            {'id': 'claude', 'name': 'Claude', 'role': 'Marksman', 'specialty': 'Damage'},
            {'id': 'esmeralda', 'name': 'Esmeralda', 'role': 'Tank', 'specialty': 'Magic Damage'},
            {'id': 'ling', 'name': 'Ling', 'role': 'Assassin', 'specialty': 'Mobility'}
        ]

        for hero in heroes_data:
            self.heroes[hero['id']] = {
                **hero,
                'win_rate': random.random() * 0.2 + 0.45,  # Random win rate 45-65%
                'ban_rate': random.random() * 0.3,  # Random ban rate 0-30%
                'pick_rate': random.random() * 0.4 + 0.1,  # Random pick rate 10-50%
                'image_path': f"assets/heroes/{hero['id']}.jpg",  # Image file path
                'counters': [],  # TODO: Add counter relationships
                'synergies': []  # TODO: Add synergy relationships
            }

        print(f'✅ Loaded {len(self.heroes)} heroes')

    def get_all_heroes(self) -> List[Dict]:
        """Get all heroes"""
        return list(self.heroes.values())

    def get_hero(self, hero_id: str) -> Optional[Dict]:
        """Get hero by ID"""
        return self.heroes.get(hero_id)

    def get_heroes_by_role(self, role: str) -> List[Dict]:
        """Get heroes by role"""
        return [hero for hero in self.get_all_heroes() if hero['role'] == role]

    def get_roles(self) -> List[str]:
        """Get available roles"""
        return self.roles

    def search_heroes(self, query: str) -> List[Dict]:
        """Search heroes by name"""
        lowercase_query = query.lower()
        return [
            hero for hero in self.get_all_heroes()
            if lowercase_query in hero['name'].lower()
        ]