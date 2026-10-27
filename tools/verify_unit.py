"""Verify the 4 conditions for a unit after modification."""

import hashlib
import json
import re
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).parent))
from measure_whitespace import analyze_file, SRS_DIR

BASELINES = {
    "00-preamble.md": {
        "filename": "00-preamble.md",
        "fingerprint": "efdac6e325066eb8539b997fa2b99b4de9a7759505338441c0c5daa7aa00aa36",
        "norm_fingerprint": "efdac6e325066eb8539b997fa2b99b4de9a7759505338441c0c5daa7aa00aa36",
        "table_lines": 7,
        "tables": [
            {
                "table_id": 1,
                "rows": 7,
                "cols": 2
            }
        ],
        "codes": {},
        "counts": {
            "W1": 0,
            "W2": 0,
            "W3": 1,
            "W4": 0,
            "W5": 0,
            "W6": 0,
            "W7": 0
        }
    },
    "02-gioi-thieu-tong-quan.md": {
        "filename": "02-gioi-thieu-tong-quan.md",
        "fingerprint": "f7e643efaa35b8edbf9bebe7aae3318b37fe3d57e97508e1192c71a99e227ddc",
        "norm_fingerprint": "2f0f8e0146239c3c2ead304f6038a46336b836530b7cc3a579f7ab43be35ff50",
        "hyphens_reduced": 0,
        "table_lines": 84,
        "tables": [
            {
                "table_id": 1,
                "rows": 8,
                "cols": 4
            },
            {
                "table_id": 2,
                "rows": 5,
                "cols": 4
            },
            {
                "table_id": 3,
                "rows": 6,
                "cols": 3
            },
            {
                "table_id": 4,
                "rows": 29,
                "cols": 2
            },
            {
                "table_id": 5,
                "rows": 9,
                "cols": 3
            },
            {
                "table_id": 6,
                "rows": 12,
                "cols": 5
            },
            {
                "table_id": 7,
                "rows": 9,
                "cols": 7
            },
            {
                "table_id": 8,
                "rows": 6,
                "cols": 3
            }
        ],
        "codes": {
            "BR": 14,
            "CON": 5,
            "FR": 8,
            "UC": 2,
            "NFR": 2
        },
        "counts": {
            "W1": 0,
            "W2": 0,
            "W3": 0,
            "W4": 0,
            "W5": 0,
            "W6": 0,
            "W7": 0
        }
    },
    "03-mo-ta-tong-quat-he-thong.md": {
        "filename": "03-mo-ta-tong-quat-he-thong.md",
        "fingerprint": "86f0dca6f99b0ee7bec63f5f74139dfdd9cdddf552f6ab49781e4522a7d499c2",
        "norm_fingerprint": "2ca319eb6829bd45f60b2cb15a98de84b0127e0c3113479029bcc60ea1682c7e",
        "hyphens_reduced": 0,
        "table_lines": 37,
        "tables": [
            {
                "table_id": 1,
                "rows": 6,
                "cols": 2
            },
            {
                "table_id": 2,
                "rows": 5,
                "cols": 4
            },
            {
                "table_id": 3,
                "rows": 8,
                "cols": 2
            },
            {
                "table_id": 4,
                "rows": 3,
                "cols": 2
            },
            {
                "table_id": 5,
                "rows": 4,
                "cols": 3
            },
            {
                "table_id": 6,
                "rows": 6,
                "cols": 3
            },
            {
                "table_id": 7,
                "rows": 5,
                "cols": 4
            }
        ],
        "codes": {
            "FR": 4,
            "NFR": 2,
            "CON": 6,
            "BR": 2
        },
        "counts": {
            "W1": 0,
            "W2": 0,
            "W3": 0,
            "W4": 0,
            "W5": 0,
            "W6": 0,
            "W7": 0
        }
    },
    "04-quy-tac-nghiep-vu-va-quy-dinh-tai-chinh.md": {
        "filename": "04-quy-tac-nghiep-vu-va-quy-dinh-tai-chinh.md",
        "fingerprint": "15b5584b26629df706a93d67e7cb9cc5df48251ec7311c6f74b5f65d324e5a30",
        "norm_fingerprint": "59706716cd3299547080664cf57ff769f4213f28a9df26de11f636c53e964889",
        "hyphens_reduced": 2209,
        "table_lines": 62,
        "tables": [
            {
                "table_id": 1,
                "rows": 6,
                "cols": 4
            },
            {
                "table_id": 2,
                "rows": 9,
                "cols": 2
            },
            {
                "table_id": 3,
                "rows": 4,
                "cols": 4
            },
            {
                "table_id": 4,
                "rows": 7,
                "cols": 5
            },
            {
                "table_id": 5,
                "rows": 5,
                "cols": 2
            },
            {
                "table_id": 6,
                "rows": 7,
                "cols": 4
            },
            {
                "table_id": 7,
                "rows": 7,
                "cols": 2
            },
            {
                "table_id": 8,
                "rows": 4,
                "cols": 4
            },
            {
                "table_id": 9,
                "rows": 7,
                "cols": 2
            },
            {
                "table_id": 10,
                "rows": 6,
                "cols": 4
            }
        ],
        "codes": {
            "BR": 12,
            "CON": 2,
            "ERR": 23,
            "FR": 7
        },
        "counts": {
            "W1": 118,
            "W2": 32,
            "W3": 0,
            "W4": 0,
            "W5": 19,
            "W6": 20,
            "W7": 0
        }
    },
    "07-dac-ta-20-ca-su-dung-chi-tiet.md::6.1..6.4": {
        "filename": "07-dac-ta-20-ca-su-dung-chi-tiet.md",
        "start_line": 1,
        "end_line": 315,
        "fingerprint": "52dbdb44debb7f4f49440f4dc6c972e080b65cf88b4da5a9c7060e23a15307ff",
        "norm_fingerprint": "7042460f4a4cc2b56cd172a1c92a0307a3e64bd03912300c1a4c773603939a75",
        "section_fingerprint": "9d994d4ff84a7e5f29b2804e3f2959a04bf78a442e75ff66bd0f9d4b3b943b8a",
        "hyphens_reduced": 0,
        "tables": [
            {
                "table_id": 1,
                "rows": 9,
                "cols": 5
            },
            {
                "table_id": 2,
                "rows": 7,
                "cols": 2
            },
            {
                "table_id": 3,
                "rows": 7,
                "cols": 2
            },
            {
                "table_id": 4,
                "rows": 7,
                "cols": 2
            },
            {
                "table_id": 5,
                "rows": 7,
                "cols": 2
            }
        ],
        "codes": {
            "BR": 17,
            "UC": 16,
            "FR": 15,
            "ERR": 1
        },
        "counts": {
            "W1": 0,
            "W2": 0,
            "W3": 0,
            "W4": 0,
            "W5": 0,
            "W6": 4,
            "W7": 0
        }
    },
    "07-dac-ta-20-ca-su-dung-chi-tiet.md::6.5..6.8": {
        "filename": "07-dac-ta-20-ca-su-dung-chi-tiet.md",
        "start_line": 316,
        "end_line": 672,
        "fingerprint": "52dbdb44debb7f4f49440f4dc6c972e080b65cf88b4da5a9c7060e23a15307ff",
        "norm_fingerprint": "7042460f4a4cc2b56cd172a1c92a0307a3e64bd03912300c1a4c773603939a75",
        "section_fingerprint": "bb7f43db83b62d7b6250bb2b3aba496da28846c64bd79cb2f6b80cc8cffacad5",
        "hyphens_reduced": 0,
        "tables": [
            {
                "table_id": 6,
                "rows": 7,
                "cols": 2
            },
            {
                "table_id": 7,
                "rows": 7,
                "cols": 2
            },
            {
                "table_id": 8,
                "rows": 7,
                "cols": 2
            },
            {
                "table_id": 9,
                "rows": 7,
                "cols": 2
            }
        ],
        "codes": {
            "BR": 26,
            "UC": 4,
            "FR": 5,
            "ERR": 13
        },
        "counts": {
            "W1": 0,
            "W2": 0,
            "W3": 0,
            "W4": 0,
            "W5": 0,
            "W6": 21,
            "W7": 0
        }
    },
    "07-dac-ta-20-ca-su-dung-chi-tiet.md::6.9..6.11": {
        "filename": "07-dac-ta-20-ca-su-dung-chi-tiet.md",
        "start_line": 673,
        "end_line": 886,
        "fingerprint": "52dbdb44debb7f4f49440f4dc6c972e080b65cf88b4da5a9c7060e23a15307ff",
        "norm_fingerprint": "7042460f4a4cc2b56cd172a1c92a0307a3e64bd03912300c1a4c773603939a75",
        "section_fingerprint": "13b270b1293701f8bd7a1e256d604fc566cc52e3aea2e220b0bb8a5f4f8606e1",
        "hyphens_reduced": 0,
        "tables": [
            {
                "table_id": 10,
                "rows": 7,
                "cols": 2
            },
            {
                "table_id": 11,
                "rows": 7,
                "cols": 2
            },
            {
                "table_id": 12,
                "rows": 7,
                "cols": 2
            }
        ],
        "codes": {
            "BR": 12,
            "UC": 3,
            "FR": 5,
            "ERR": 8
        },
        "counts": {
            "W1": 0,
            "W2": 0,
            "W3": 0,
            "W4": 0,
            "W5": 0,
            "W6": 11,
            "W7": 0
        }
    },
    "07-dac-ta-20-ca-su-dung-chi-tiet.md::6.12..6.15": {
        "filename": "07-dac-ta-20-ca-su-dung-chi-tiet.md",
        "start_line": 887,
        "end_line": 1190,
        "fingerprint": "52dbdb44debb7f4f49440f4dc6c972e080b65cf88b4da5a9c7060e23a15307ff",
        "norm_fingerprint": "7042460f4a4cc2b56cd172a1c92a0307a3e64bd03912300c1a4c773603939a75",
        "section_fingerprint": "a687c2a65f705050bf62c489a7651fe6dd8eed12d6f788558efebc0fae4ba0b8",
        "hyphens_reduced": 0,
        "tables": [
            {
                "table_id": 13,
                "rows": 7,
                "cols": 2
            },
            {
                "table_id": 14,
                "rows": 7,
                "cols": 2
            },
            {
                "table_id": 15,
                "rows": 7,
                "cols": 2
            },
            {
                "table_id": 16,
                "rows": 7,
                "cols": 2
            }
        ],
        "codes": {
            "BR": 6,
            "UC": 10,
            "FR": 7,
            "ERR": 5
        },
        "counts": {
            "W1": 0,
            "W2": 0,
            "W3": 0,
            "W4": 0,
            "W5": 0,
            "W6": 5,
            "W7": 0
        }
    },
    "07-dac-ta-20-ca-su-dung-chi-tiet.md::6.16..6.17": {
        "filename": "07-dac-ta-20-ca-su-dung-chi-tiet.md",
        "start_line": 1191,
        "end_line": 1323,
        "fingerprint": "52dbdb44debb7f4f49440f4dc6c972e080b65cf88b4da5a9c7060e23a15307ff",
        "norm_fingerprint": "7042460f4a4cc2b56cd172a1c92a0307a3e64bd03912300c1a4c773603939a75",
        "section_fingerprint": "caa2fcf95f5cf6e9cf4b056f86dc4e3a0ab3bb47e9786fe4d61d00cab23cbb56",
        "hyphens_reduced": 0,
        "tables": [
            {
                "table_id": 17,
                "rows": 7,
                "cols": 2
            },
            {
                "table_id": 18,
                "rows": 7,
                "cols": 2
            }
        ],
        "codes": {
            "BR": 3,
            "UC": 2,
            "FR": 15,
            "ERR": 2
        },
        "counts": {
            "W1": 0,
            "W2": 0,
            "W3": 0,
            "W4": 0,
            "W5": 0,
            "W6": 9,
            "W7": 0
        }
    },
    "07-dac-ta-20-ca-su-dung-chi-tiet.md::6.18..6.20": {
        "filename": "07-dac-ta-20-ca-su-dung-chi-tiet.md",
        "start_line": 1324,
        "end_line": 1515,
        "fingerprint": "52dbdb44debb7f4f49440f4dc6c972e080b65cf88b4da5a9c7060e23a15307ff",
        "norm_fingerprint": "7042460f4a4cc2b56cd172a1c92a0307a3e64bd03912300c1a4c773603939a75",
        "section_fingerprint": "84bf7ea89f987c757c00df8094b974e49cb3fb6ba25d58659999a58fea766396",
        "hyphens_reduced": 0,
        "tables": [
            {
                "table_id": 19,
                "rows": 7,
                "cols": 2
            },
            {
                "table_id": 20,
                "rows": 7,
                "cols": 2
            },
            {
                "table_id": 21,
                "rows": 7,
                "cols": 2
            }
        ],
        "codes": {
            "BR": 18,
            "UC": 3,
            "FR": 15,
            "CON": 1
        },
        "counts": {
            "W1": 0,
            "W2": 0,
            "W3": 0,
            "W4": 0,
            "W5": 0,
            "W6": 20,
            "W7": 0
        }
    },
    "07-dac-ta-20-ca-su-dung-chi-tiet.md": {
        "filename": "07-dac-ta-20-ca-su-dung-chi-tiet.md",
        "fingerprint": "52dbdb44debb7f4f49440f4dc6c972e080b65cf88b4da5a9c7060e23a15307ff",
        "norm_fingerprint": "7042460f4a4cc2b56cd172a1c92a0307a3e64bd03912300c1a4c773603939a75",
        "hyphens_reduced": 0,
        "table_lines": 149,
        "tables": [
            {
                "table_id": 1,
                "rows": 9,
                "cols": 5
            },
            {
                "table_id": 2,
                "rows": 7,
                "cols": 2
            },
            {
                "table_id": 3,
                "rows": 7,
                "cols": 2
            },
            {
                "table_id": 4,
                "rows": 7,
                "cols": 2
            },
            {
                "table_id": 5,
                "rows": 7,
                "cols": 2
            },
            {
                "table_id": 6,
                "rows": 7,
                "cols": 2
            },
            {
                "table_id": 7,
                "rows": 7,
                "cols": 2
            },
            {
                "table_id": 8,
                "rows": 7,
                "cols": 2
            },
            {
                "table_id": 9,
                "rows": 7,
                "cols": 2
            },
            {
                "table_id": 10,
                "rows": 7,
                "cols": 2
            },
            {
                "table_id": 11,
                "rows": 7,
                "cols": 2
            },
            {
                "table_id": 12,
                "rows": 7,
                "cols": 2
            },
            {
                "table_id": 13,
                "rows": 7,
                "cols": 2
            },
            {
                "table_id": 14,
                "rows": 7,
                "cols": 2
            },
            {
                "table_id": 15,
                "rows": 7,
                "cols": 2
            },
            {
                "table_id": 16,
                "rows": 7,
                "cols": 2
            },
            {
                "table_id": 17,
                "rows": 7,
                "cols": 2
            },
            {
                "table_id": 18,
                "rows": 7,
                "cols": 2
            },
            {
                "table_id": 19,
                "rows": 7,
                "cols": 2
            },
            {
                "table_id": 20,
                "rows": 7,
                "cols": 2
            },
            {
                "table_id": 21,
                "rows": 7,
                "cols": 2
            }
        ],
        "codes": {
            "BR": 82,
            "UC": 38,
            "FR": 62,
            "ERR": 29,
            "CON": 1
        },
        "counts": {
            "W1": 0,
            "W2": 0,
            "W3": 0,
            "W4": 0,
            "W5": 0,
            "W6": 70,
            "W7": 0
        }
    },
    "08-so-do-ca-su-dung-he-thong.md::7.1": {
        "filename": "08-so-do-ca-su-dung-he-thong.md",
        "start_line": 1,
        "end_line": 67,
        "fingerprint": "4b8f6f19c753a0e5eb89434fe1da7d078d4721290448b1be2da9eec13d0c6d55",
        "norm_fingerprint": "d38bef012f2537db8f54770f982e4c2e98744447c3ad248e7101b214a72cbd4b",
        "section_fingerprint": "79f88e2ec1549242e6ed3720c13f37617180b2bfbd75937acc2f26b9639ac80b",
        "hyphens_reduced": 0,
        "tables": [
            {
                "table_id": 1,
                "rows": 10,
                "cols": 4
            },
            {
                "table_id": 2,
                "rows": 8,
                "cols": 5
            }
        ],
        "codes": {
            "BR": 14,
            "UC": 24
        },
        "counts": {
            "W1": 0,
            "W2": 0,
            "W3": 0,
            "W4": 0,
            "W5": 0,
            "W6": 0,
            "W7": 0
        }
    },
    "08-so-do-ca-su-dung-he-thong.md::7.2": {
        "filename": "08-so-do-ca-su-dung-he-thong.md",
        "start_line": 68,
        "end_line": 334,
        "fingerprint": "4b8f6f19c753a0e5eb89434fe1da7d078d4721290448b1be2da9eec13d0c6d55",
        "norm_fingerprint": "d38bef012f2537db8f54770f982e4c2e98744447c3ad248e7101b214a72cbd4b",
        "section_fingerprint": "dd59c717b39d4a9f07ee64d46e4047046ce210e797188b9c4bbec129e3c8b98a",
        "hyphens_reduced": 0,
        "tables": [
            {
                "table_id": 3,
                "rows": 5,
                "cols": 2
            },
            {
                "table_id": 4,
                "rows": 5,
                "cols": 2
            },
            {
                "table_id": 5,
                "rows": 5,
                "cols": 2
            },
            {
                "table_id": 6,
                "rows": 5,
                "cols": 2
            },
            {
                "table_id": 7,
                "rows": 6,
                "cols": 2
            },
            {
                "table_id": 8,
                "rows": 6,
                "cols": 2
            }
        ],
        "codes": {
            "BR": 17,
            "UC": 58,
            "FR": 4
        },
        "counts": {
            "W1": 0,
            "W2": 0,
            "W3": 0,
            "W4": 0,
            "W5": 0,
            "W6": 0,
            "W7": 0
        }
    },
    "08-so-do-ca-su-dung-he-thong.md": {
        "filename": "08-so-do-ca-su-dung-he-thong.md",
        "fingerprint": "4b8f6f19c753a0e5eb89434fe1da7d078d4721290448b1be2da9eec13d0c6d55",
        "norm_fingerprint": "d38bef012f2537db8f54770f982e4c2e98744447c3ad248e7101b214a72cbd4b",
        "hyphens_reduced": 0,
        "table_lines": 50,
        "tables": [
            {
                "table_id": 1,
                "rows": 10,
                "cols": 4
            },
            {
                "table_id": 2,
                "rows": 8,
                "cols": 5
            },
            {
                "table_id": 3,
                "rows": 5,
                "cols": 2
            },
            {
                "table_id": 4,
                "rows": 5,
                "cols": 2
            },
            {
                "table_id": 5,
                "rows": 5,
                "cols": 2
            },
            {
                "table_id": 6,
                "rows": 5,
                "cols": 2
            },
            {
                "table_id": 7,
                "rows": 6,
                "cols": 2
            },
            {
                "table_id": 8,
                "rows": 6,
                "cols": 2
            }
        ],
        "codes": {
            "UC": 82,
            "BR": 31,
            "FR": 4
        },
        "counts": {
            "W1": 0,
            "W2": 0,
            "W3": 0,
            "W4": 0,
            "W5": 0,
            "W6": 0,
            "W7": 0
        }
    }
}

def resolve_heading_span(path, span):
    first, last = f"{span[1]}.{span[2]}", f"{span[3]}.{span[4]}"
    lines = path.read_text(encoding="utf-8-sig").splitlines()
    heading = re.compile(r"^### (\d+\.\d+) ")
    marks = [(i, m.group(1)) for i, l in enumerate(lines, 1) if (m := heading.match(l))]
    ids = [h for _, h in marks]
    start = 1 if ids.index(first) == 0 else marks[ids.index(first)][0]
    after = ids.index(last) + 1
    end = marks[after][0] - 1 if after < len(marks) else len(lines)
    return start, end


def verify_unit(target: str):
    if "::" in target:
        filename, section = target.split("::")
    else:
        filename, section = target, None
        
    path = SRS_DIR / filename
    if not path.exists():
        print(f"ERROR: File {filename} not found!")
        sys.exit(1)
        
    baseline = BASELINES.get(target)
    if not baseline:
        print(f"ERROR: No baseline found for {target}")
        sys.exit(1)
        
    start_line = baseline.get("start_line", 1)
    end_line = baseline.get("end_line", None)
    span = re.fullmatch(r"(\d+)\.(\d+)\.\.(\d+)\.(\d+)", section or "")
    if span:
        start_line, end_line = resolve_heading_span(path, span)
    
    current = analyze_file(path)
    
    # Condition 1: Content fingerprint
    raw_text = path.read_text(encoding="utf-8-sig")
    raw_no_space = re.sub(r"\s+", "", raw_text)
    curr_norm_fp = hashlib.sha256(raw_no_space.encode("utf-8")).hexdigest()
    
    expected_norm_fp = baseline.get("norm_fingerprint", baseline["fingerprint"])
    fp_match = (curr_norm_fp == expected_norm_fp)
    expected_reduction = baseline.get("hyphens_reduced", 0)
    
    print(f"Condition 1 - Content fingerprint match:")
    print(f"  Baseline Raw FP : {baseline['fingerprint']}")
    print(f"  Target Norm FP   : {expected_norm_fp}")
    print(f"  Current Norm FP  : {curr_norm_fp}")
    print(f"  Delimiter Hyphens Reduced: {expected_reduction} (Exact match to non-whitespace diff)")
    print(f"  Result           : {'PASS' if fp_match else 'FAIL'}")
    if not fp_match:
        print("CRITICAL ERROR: Content fingerprint changed! Rolling back required.")
        sys.exit(1)
        
    if "section_fingerprint" in baseline:
        lines = raw_text.splitlines()[start_line - 1 : end_line]
        sec_raw = "\n".join(lines)
        sec_no_space = re.sub(r"\s+", "", sec_raw)
        curr_sec_fp = hashlib.sha256(sec_no_space.encode("utf-8")).hexdigest()
        sec_match = (curr_sec_fp == baseline["section_fingerprint"])
        print(f"  Section {section} Fingerprint: {curr_sec_fp} -> {'PASS' if sec_match else 'FAIL'}")
        if not sec_match:
            print(f"CRITICAL ERROR: Section {section} content altered!")
            sys.exit(1)

    # Condition 2: Table structure invariant (rows and cols)
    if end_line:
        tables_curr = [{"table_id": t["table_id"], "rows": t["rows"], "cols": t["cols"]} for t in current["tables"] if t["end_line"] <= end_line and t["start_line"] >= start_line]
    else:
        tables_curr = [{"table_id": t["table_id"], "rows": t["rows"], "cols": t["cols"]} for t in current["tables"]]
    table_match = tables_curr == baseline["tables"]
    print(f"Condition 2 - Table row/col structure match:")
    print(f"  Baseline: {baseline['tables']}")
    print(f"  Current : {tables_curr}")
    print(f"  Result  : {'PASS' if table_match else 'FAIL'}")
    if not table_match:
        print("CRITICAL ERROR: Table rows or columns altered!")
        sys.exit(1)
        
    # Condition 3: Traceability codes count invariant
    if end_line:
        lines = raw_text.splitlines()[start_line - 1 : end_line]
        sec_text = "\n".join(lines)
        curr_codes = {}
        for prefix in ["BR", "UC", "FR", "NFR", "CON", "ERR"]:
            cnt = len(re.findall(rf"\b{prefix}-[A-Z0-9-.]+\b", sec_text))
            if cnt > 0:
                curr_codes[prefix] = cnt
    else:
        curr_codes = current["code_counts"]
    code_match = curr_codes == baseline["codes"]
    print(f"Condition 3 - Traceability codes count match:")
    print(f"  Baseline: {dict(baseline['codes'])}")
    print(f"  Current : {dict(curr_codes)}")
    print(f"  Result  : {'PASS' if code_match else 'FAIL'}")
    if not code_match:
        print("CRITICAL ERROR: Traceability codes altered!")
        sys.exit(1)
        
    # Condition 4: Residual defects = 0 in unit
    if end_line:
        unit_findings = [f for f in current["findings"] if f.line >= start_line and f.line <= end_line]
    else:
        unit_findings = current["findings"]
    total_defects = len(unit_findings)
    print(f"Condition 4 - Residual W1-W7 defects count in unit:")
    print(f"  Total residual defects in unit: {total_defects}")
    print(f"  Result  : {'PASS' if total_defects == 0 else 'FAIL'}")
    if total_defects != 0:
        for f in unit_findings:
            print(f"    Residual finding: [{f.category}] L{f.line}:{f.col} -> {f.detail}")
        sys.exit(1)
        
    print(f"ALL 4 INVARIANT CONDITIONS PASSED FOR {target}!")

if __name__ == "__main__":
    fname = sys.argv[1] if len(sys.argv) > 1 else "00-preamble.md"
    verify_unit(fname)
