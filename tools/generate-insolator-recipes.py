import json

ITEMS = [
    "minecraft:acacia_leaves",
    "minecraft:acacia_sapling",
    "minecraft:allium",
    "minecraft:azalea",
    "minecraft:azalea_leaves",
    "minecraft:azure_bluet",
    "minecraft:bamboo",
    "minecraft:beetroot_seeds",
    "minecraft:big_dripleaf",
    "minecraft:birch_leaves",
    "minecraft:birch_sapling",
    "minecraft:blue_orchid",
    "minecraft:brown_mushroom",
    "minecraft:cactus",
    "minecraft:chorus_flower",
    "minecraft:cornflower",
    "minecraft:crimson_fungus",
    "minecraft:crimson_roots",
    "minecraft:dandelion",
    "minecraft:dark_oak_leaves",
    "minecraft:dark_oak_sapling",
    "minecraft:fern",
    "minecraft:flowering_azalea",
    "minecraft:flowering_azalea_leaves",
    "minecraft:glow_lichen",
    "minecraft:grass",
    "minecraft:hanging_roots",
    "minecraft:jungle_leaves",
    "minecraft:jungle_sapling",
    "minecraft:large_fern",
    "minecraft:lilac",
    "minecraft:lily_of_the_valley",
    "minecraft:kelp",
    "minecraft:mangrove_leaves",
    "minecraft:melon",
    "minecraft:moss_block",
    "minecraft:moss_carpet",
    "minecraft:nether_sprouts",
    "minecraft:nether_wart",
    "minecraft:oak_leaves",
    "minecraft:oak_sapling",
    "minecraft:orange_tulip",
    "minecraft:oxeye_daisy",
    "minecraft:peony",
    "minecraft:pink_tulip",
    "minecraft:poppy",
    "minecraft:potato",
    "minecraft:pumpkin",
    "minecraft:red_mushroom",
    "minecraft:red_tulip",
    "minecraft:rose_bush",
    "minecraft:sea_pickle",
    "minecraft:seagrass",
    # "minecraft:short_grass", TODO: 1.20 (replaces minecraft:grass)
    "minecraft:small_dripleaf",
    "minecraft:spore_blossom",
    "minecraft:spruce_leaves",
    "minecraft:spruce_sapling",
    "minecraft:sugar_cane",
    "minecraft:sunflower",
    "minecraft:tall_grass",
    "minecraft:twisting_vines",
    "minecraft:vine",
    "minecraft:warped_fungus",
    "minecraft:warped_roots",
    "minecraft:weeping_vines",
    "minecraft:wheat",
    "minecraft:wheat_seeds",
    "minecraft:white_tulip",
    "minecraft:wither_rose",
    # "minecraft:cherry_leaves", TODO: 1.20
    # "minecraft:cherry_sapling", TODO: 1.20
    # "minecraft:pink_petals", TODO: 1.20
    # "minecraft:pitcher_plant", TODO: 1.20
    # "minecraft:torchflower", TODO: 1.20
]

def basicTicksFor(item: str) -> int:
    if item.endswith("_leaves") or item.endswith("_sapling"):
        return 1000
    return 500

def basicWaterFor(item: str) -> int:
    if item.endswith("_leaves") or item.endswith("_sapling"):
        return 1000
    return 500

for item in ITEMS:
    namespace, item_name = item.split(":")
    with open(f"src/main/resources/data/utamacraft/recipes/insolator_{namespace}_{item_name}.json", "w") as fp:
        fp.write(json.dumps({
            "type": "utamacraft:insolator",
            "input": { "item":  item },
            "ticks": basicTicksFor(item),
            "fluid": {
                "FluidName": "minecraft:water",
                "Amount": basicWaterFor(item)
            },
            "output": { "item": item, "count": 2 }
        }, indent=2))

with open("insolator-recipes.md", "w") as fp:
    for item in ITEMS:
        ticks = basicTicksFor(item)
        energy = 32 * ticks
        water = basicWaterFor(item)
        fp.write(f"| [{item}] | {water} | {energy} | {ticks} | {ticks / 20} |\n")
